package org.dromara.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.v7.core.convert.ConvertUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.v7.core.util.ObjUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.file.FileUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatisflex.core.page.PageQuery;
import org.dromara.common.mybatisflex.core.page.TableDataInfo;
import org.dromara.common.oss.core.OssClient;
import org.dromara.common.oss.entity.UploadResult;
import org.dromara.common.oss.enums.AccessPolicyType;
import org.dromara.common.oss.factory.OssFactory;
import org.dromara.common.oss.exception.OssException;
import org.dromara.resource.domain.SysOss;
import org.dromara.resource.domain.SysOssExt;
import org.dromara.resource.domain.bo.SysOssBo;
import org.dromara.resource.domain.bo.SysOssCompleteBo;
import org.dromara.resource.domain.bo.SysOssMultipartAbortBo;
import org.dromara.resource.domain.bo.SysOssMultipartCompleteBo;
import org.dromara.resource.domain.bo.SysOssMultipartInitBo;
import org.dromara.resource.domain.bo.SysOssMultipartPartBo;
import org.dromara.resource.domain.bo.SysOssPresignBo;
import org.dromara.resource.domain.vo.SysOssMultipartInitVo;
import org.dromara.resource.domain.vo.SysOssMultipartPartVo;
import org.dromara.resource.domain.vo.SysOssPresignVo;
import org.dromara.resource.domain.vo.SysOssUploadVo;
import org.dromara.resource.domain.vo.SysOssVo;
import org.dromara.resource.mapper.SysOssMapper;
import org.dromara.resource.service.ISysOssConfigService;
import org.dromara.resource.service.ISysOssService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文件上传 服务层实现
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@Service
public class SysOssServiceImpl implements ISysOssService {

    private final SysOssMapper baseMapper;
    private final ISysOssConfigService ossConfigService;

    /**
     * 查询OSS对象存储列表
     *
     * @param bo        OSS对象存储分页查询对象
     * @param pageQuery 分页查询实体类
     * @return 结果
     */
    @Override
    public TableDataInfo<SysOssVo> queryPageList(SysOssBo bo, PageQuery pageQuery) {
        QueryWrapper wrapper = buildQueryWrapper(bo);
        Page<SysOssVo> result = baseMapper.selectVoPage(pageQuery.build(), wrapper);
        List<SysOssVo> filterResult = result.getRecords().stream().map(this::matchingUrl).collect(Collectors.toList());
        result.setRecords(filterResult);
        return TableDataInfo.build(result);
    }

    /**
     * 根据一组 ossIds 获取对应的 SysOssVo 列表
     *
     * @param ossIds 一组文件在数据库中的唯一标识集合
     * @return 包含 SysOssVo 对象的列表
     */
    @Override
    public List<SysOssVo> listByIds(Collection<Long> ossIds) {
        List<SysOssVo> list = new ArrayList<>();
        SysOssServiceImpl ossService = SpringUtils.getAopProxy(this);
        for (Long id : ossIds) {
            SysOssVo vo = ossService.getById(id);
            if (ObjUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo));
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo);
                }
            }
        }
        return list;
    }

    /**
     * 根据一组 ossIds 获取对应文件的 URL 列表
     *
     * @param ossIds 以逗号分隔的 ossId 字符串
     * @return 以逗号分隔的文件 URL 字符串
     */
    @Override
    public String selectUrlByIds(String ossIds) {
        List<String> list = new ArrayList<>();
        SysOssServiceImpl ossService = SpringUtils.getAopProxy(this);
        for (Long id : StringUtils.splitTo(ossIds, ConvertUtil::toLong)) {
            SysOssVo vo = ossService.getById(id);
            if (ObjUtil.isNotNull(vo)) {
                try {
                    list.add(this.matchingUrl(vo).getUrl());
                } catch (Exception ignored) {
                    // 如果oss异常无法连接则将数据直接返回
                    list.add(vo.getUrl());
                }
            }
        }
        return StringUtils.joinComma(list);
    }

    private QueryWrapper buildQueryWrapper(SysOssBo bo) {
        Map<String, Object> params = bo.getParams();
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (StringUtils.isNotBlank(bo.getFileName())) {
            queryWrapper.and("file_name like ?", "%" + bo.getFileName() + "%");
        }
        if (StringUtils.isNotBlank(bo.getOriginalName())) {
            queryWrapper.and("original_name like ?", "%" + bo.getOriginalName() + "%");
        }
        if (StringUtils.isNotBlank(bo.getFileSuffix())) {
            queryWrapper.and("file_suffix = ?", bo.getFileSuffix());
        }
        if (StringUtils.isNotBlank(bo.getUrl())) {
            queryWrapper.and("url = ?", bo.getUrl());
        }
        if (params.get("beginCreateTime") != null && params.get("endCreateTime") != null) {
            queryWrapper.and("create_time between ? and ?", params.get("beginCreateTime"), params.get("endCreateTime"));
        }
        if (ObjUtil.isNotNull(bo.getCreateBy())) {
            queryWrapper.and("create_by = ?", bo.getCreateBy());
        }
        if (StringUtils.isNotBlank(bo.getService())) {
            queryWrapper.and("service = ?", bo.getService());
        }
        queryWrapper.orderBy("oss_id", true);
        return queryWrapper;
    }

    /**
     * 根据 ossId 从缓存或数据库中获取 SysOssVo 对象
     *
     * @param ossId 文件在数据库中的唯一标识
     * @return SysOssVo 对象，包含文件信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_OSS, key = "#ossId")
    @Override
    public SysOssVo getById(Long ossId) {
        return baseMapper.selectVoById(ossId);
    }

    /**
     * 文件下载方法，支持一次性下载完整文件
     *
     * @param ossId    OSS对象ID
     * @param response HttpServletResponse对象，用于设置响应头和向客户端发送文件内容
     */
    @Override
    public void download(Long ossId, HttpServletResponse response) throws IOException {
        SysOssVo sysOss = SpringUtils.getAopProxy(this).getById(ossId);
        if (ObjUtil.isNull(sysOss)) {
            throw new ServiceException("文件数据不存在!");
        }
        FileUtils.setAttachmentResponseHeader(response, sysOss.getOriginalName());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + "; charset=UTF-8");
        OssClient storage = getOssClient(sysOss.getService());
        storage.download(sysOss.getFileName(), response.getOutputStream(), response::setContentLengthLong);
    }

    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     * @throws ServiceException 如果上传过程中发生异常，则抛出 ServiceException 异常
     */
    @Override
    public SysOssVo upload(MultipartFile file) {
        if (ObjUtil.isNull(file) || file.isEmpty()) {
            throw new ServiceException("上传文件不能为空");
        }
        String originalfileName = file.getOriginalFilename();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = getOssClient(null);
        UploadResult uploadResult;
        try {
            uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        SysOssExt ext1 = new SysOssExt();
        ext1.setFileSize(file.getSize());
        ext1.setContentType(file.getContentType());
        // 保存文件信息
        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult, ext1);
    }

    /**
     * 上传文件到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的文件对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     */
    @Override
    public SysOssVo upload(File file) {
        if (ObjUtil.isNull(file) || !file.isFile() || file.length() <= 0) {
            throw new ServiceException("上传文件不能为空");
        }
        String originalfileName = file.getName();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = getOssClient(null);
        long length = file.length();
        UploadResult uploadResult = storage.uploadSuffix(file, suffix);
        SysOssExt ext1 = new SysOssExt();
        ext1.setFileSize(length);
        // 保存文件信息
        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult, ext1);
    }

    @Override
    public SysOssPresignVo presign(SysOssPresignBo bo) {
        if (ObjUtil.isNull(bo) || StringUtils.isBlank(bo.getFileName())) {
            throw new ServiceException("文件名不能为空");
        }
        OssClient storage = getOssClient(bo.getService());
        String suffix = getSuffix(bo.getFileName());
        String objectKey = buildObjectKey(storage, bo.getBizType(), suffix);
        Duration expire = Duration.ofMinutes(10);
        String uploadUrl = storage.getPresignedPutUrl(objectKey, expire, bo.getContentType());
        SysOssPresignVo vo = new SysOssPresignVo();
        vo.setUploadUrl(uploadUrl);
        vo.setObjectKey(objectKey);
        vo.setExpireAt(System.currentTimeMillis() + expire.toMillis());
        vo.setMethod("PUT");
        return vo;
    }

    @Override
    public SysOssUploadVo complete(SysOssCompleteBo bo) {
        if (ObjUtil.isNull(bo) || StringUtils.isBlank(bo.getObjectKey())) {
            throw new ServiceException("objectKey不能为空");
        }
        OssClient storage = getOssClient(bo.getService());
        String objectKey = normalizeObjectKey(storage, bo.getObjectKey());
        if (!storage.exists(objectKey)) {
            throw new ServiceException("对象不存在或未上传完成");
        }
        String originalName = StringUtils.isNotBlank(bo.getOriginalName()) ? bo.getOriginalName() : objectKey;
        String suffix = getSuffix(originalName);
        UploadResult uploadResult = UploadResult.builder()
            .filename(objectKey)
            .url(storage.getUrl() + StringUtils.SLASH + objectKey)
            .build();
        SysOssExt ext1 = new SysOssExt();
        ext1.setFileSize(bo.getFileSize());
        ext1.setContentType(bo.getContentType());
        SysOssVo oss = buildResultEntity(originalName, suffix, storage.getConfigKey(), uploadResult, ext1);
        return toUploadVo(oss, originalName);
    }

    @Override
    public SysOssMultipartInitVo multipartInit(SysOssMultipartInitBo bo) {
        if (ObjUtil.isNull(bo) || StringUtils.isBlank(bo.getFileName())) {
            throw new ServiceException("文件名不能为空");
        }
        OssClient storage = getOssClient(bo.getService());
        String suffix = getSuffix(bo.getFileName());
        String objectKey = buildObjectKey(storage, bo.getBizType(), suffix);
        String uploadId = storage.createMultipartUpload(objectKey);
        int partCount = resolvePartCount(bo);
        Duration expire = Duration.ofMinutes(10);
        List<SysOssMultipartPartVo> parts = new ArrayList<>();
        for (int i = 1; i <= partCount; i++) {
            SysOssMultipartPartVo partVo = new SysOssMultipartPartVo();
            partVo.setPartNumber(i);
            partVo.setUploadUrl(storage.getPresignedUploadPartUrl(objectKey, uploadId, i, expire));
            partVo.setExpireAt(System.currentTimeMillis() + expire.toMillis());
            parts.add(partVo);
        }
        SysOssMultipartInitVo vo = new SysOssMultipartInitVo();
        vo.setUploadId(uploadId);
        vo.setObjectKey(objectKey);
        vo.setParts(parts);
        vo.setExpireAt(System.currentTimeMillis() + expire.toMillis());
        return vo;
    }

    @Override
    public SysOssUploadVo multipartComplete(SysOssMultipartCompleteBo bo) {
        if (ObjUtil.isNull(bo) || StringUtils.isBlank(bo.getUploadId()) || StringUtils.isBlank(bo.getObjectKey())) {
            throw new ServiceException("uploadId或objectKey不能为空");
        }
        if (CollUtil.isEmpty(bo.getParts())) {
            throw new ServiceException("分片列表不能为空");
        }
        OssClient storage = getOssClient(bo.getService());
        String objectKey = normalizeObjectKey(storage, bo.getObjectKey());
        List<CompletedPart> parts = bo.getParts().stream()
            .sorted(Comparator.comparing(SysOssMultipartPartBo::getPartNumber))
            .map(part -> CompletedPart.builder()
                .partNumber(part.getPartNumber())
                .eTag(normalizeEtag(part.getETag()))
                .build())
            .collect(Collectors.toList());
        storage.completeMultipartUpload(objectKey, bo.getUploadId(), parts);
        String originalName = StringUtils.isNotBlank(bo.getOriginalName()) ? bo.getOriginalName() : objectKey;
        String suffix = getSuffix(originalName);
        UploadResult uploadResult = UploadResult.builder()
            .filename(objectKey)
            .url(storage.getUrl() + StringUtils.SLASH + objectKey)
            .build();
        SysOssExt ext1 = new SysOssExt();
        ext1.setFileSize(bo.getFileSize());
        ext1.setContentType(bo.getContentType());
        SysOssVo oss = buildResultEntity(originalName, suffix, storage.getConfigKey(), uploadResult, ext1);
        return toUploadVo(oss, originalName);
    }

    @Override
    public Boolean multipartAbort(SysOssMultipartAbortBo bo) {
        if (ObjUtil.isNull(bo) || StringUtils.isBlank(bo.getUploadId()) || StringUtils.isBlank(bo.getObjectKey())) {
            throw new ServiceException("uploadId或objectKey不能为空");
        }
        OssClient storage = getOssClient(bo.getService());
        String objectKey = normalizeObjectKey(storage, bo.getObjectKey());
        storage.abortMultipartUpload(objectKey, bo.getUploadId());
        return true;
    }

    private SysOssVo buildResultEntity(String originalfileName, String suffix, String configKey, UploadResult uploadResult, SysOssExt ext1) {
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setService(configKey);
        oss.setExt1(JsonUtils.toJsonString(ext1));
        baseMapper.insert(oss);
        SysOssVo sysOssVo = MapstructUtils.convert(oss, SysOssVo.class);
        return this.matchingUrl(sysOssVo);
    }

    private SysOssUploadVo toUploadVo(SysOssVo oss, String originalName) {
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(originalName);
        uploadVo.setOssId(String.valueOf(oss.getOssId()));
        return uploadVo;
    }

    /**
     * 新增OSS对象存储
     *
     * @param bo SysOssBo 对象，包含待插入的数据
     * @return 插入操作是否成功的布尔值
     */
    @Override
    public Boolean insertByBo(SysOssBo bo) {
        SysOss oss = BeanUtil.toBean(bo, SysOss.class);
        boolean flag = baseMapper.insert(oss) > 0;
        if (flag) {
            bo.setOssId(oss.getOssId());
        }
        return flag;
    }

    /**
     * 删除OSS对象存储
     *
     * @param ids     OSS对象ID串
     * @param isValid 判断是否需要校验
     * @return 结果
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        List<SysOss> list = baseMapper.selectListByIds(ids);
        for (SysOss sysOss : list) {
            OssClient storage = getOssClient(sysOss.getService());
            storage.delete(sysOss.getUrl());
        }
        return baseMapper.deleteBatchByIds(ids) > 0;
    }

    /**
     * 桶类型为 private 的URL 修改为临时URL时长为120s
     *
     * @param oss OSS对象
     * @return oss 匹配Url的OSS对象
     */
    private SysOssVo matchingUrl(SysOssVo oss) {
        OssClient storage = getOssClient(oss.getService());
        // 仅修改桶类型为 private 的URL，临时URL时长为120s
        if (AccessPolicyType.PRIVATE == storage.getAccessPolicy()) {
            oss.setUrl(storage.createPresignedGetUrl(oss.getFileName(), Duration.ofSeconds(120)));
        }
        return oss;
    }

    private OssClient getOssClient(String service) {
        try {
            return StringUtils.isNotBlank(service) ? OssFactory.instance(service) : OssFactory.instance();
        } catch (OssException e) {
            ossConfigService.init();
            return StringUtils.isNotBlank(service) ? OssFactory.instance(service) : OssFactory.instance();
        }
    }

    private String buildObjectKey(OssClient storage, String bizType, String suffix) {
        String prefix = storage.getPrefix();
        if (StringUtils.isNotBlank(bizType)) {
            prefix = StringUtils.isNotBlank(prefix) ? prefix + StringUtils.SLASH + bizType : bizType;
        }
        return storage.getPath(prefix, suffix);
    }

    private String getSuffix(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return "";
        }
        int idx = fileName.lastIndexOf('.');
        return idx >= 0 ? fileName.substring(idx) : "";
    }

    private int resolvePartCount(SysOssMultipartInitBo bo) {
        if (bo.getPartCount() != null && bo.getPartCount() > 0) {
            return bo.getPartCount();
        }
        if (bo.getFileSize() != null && bo.getPartSize() != null && bo.getPartSize() > 0) {
            return (int) Math.ceil((double) bo.getFileSize() / (double) bo.getPartSize());
        }
        throw new ServiceException("分片数量或分片大小不能为空");
    }

    private String normalizeObjectKey(OssClient storage, String objectKey) {
        if (StringUtils.isBlank(objectKey)) {
            return objectKey;
        }
        if (objectKey.startsWith("http://") || objectKey.startsWith("https://")) {
            return storage.removeBaseUrl(objectKey);
        }
        return objectKey;
    }

    private String normalizeEtag(String eTag) {
        if (StringUtils.isBlank(eTag)) {
            return eTag;
        }
        return eTag.replace("\"", "");
    }

}
