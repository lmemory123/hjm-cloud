# 哈基哈米开放 API SDK 示例

更新时间：2026-05-27

本文档提供在不同编程语言中调用哈基哈米开放 API v1 的代码示例。

## 1. Java (OkHttp)

```java
import okhttp3.*;
import java.io.IOException;

public class HakimiClient {
    private static final String BASE_URL = "https://api.hajihami.com/music/open/api/v1";
    private static final String API_KEY = "dev_your_token_here";
    private final OkHttpClient client = new OkHttpClient();

    public void getSongList() throws IOException {
        Request request = new HttpRequest.Builder()
            .url(BASE_URL + "/songs?keyword=测试&pageSize=10")
            .header("X-Hakimi-Api-Key", API_KEY)
            .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
```

## 2. Python (Requests)

```python
import requests

class HakimiClient:
    def __init__(self, api_key):
        self.base_url = "https://api.hajihami.com/music/open/api/v1"
        self.headers = {"X-Hakimi-Api-Key": api_key}

    def get_song(self, song_id):
        url = f"{self.base_url}/songs/{song_id}"
        response = requests.get(url, headers=self.headers)
        return response.json()

# 使用示例
client = HakimiClient("dev_xxx")
print(client.get_song(12345))
```

## 3. Node.js (Axios)

```javascript
const axios = require('axios');

const client = axios.create({
  baseURL: 'https://api.hajihami.com/music/open/api/v1',
  headers: { 'X-Hakimi-Api-Key': 'dev_xxx' }
});

async function searchSongs(keyword) {
  try {
    const { data } = await client.get('/songs', { params: { keyword } });
    console.log(data.rows);
  } catch (error) {
    console.error('Request failed', error.response.status);
  }
}
```

## 4. Go (Native)

```go
package main

import (
	"fmt"
	"io/ioutil"
	"net/http"
)

func main() {
	req, _ := http.NewRequest("GET", "https://api.hajihami.com/music/open/api/v1/meta", nil)
	req.Header.Set("X-Hakimi-Api-Key", "dev_xxx")

	client := &http.Client{}
	resp, _ := client.Do(req)
	defer resp.Body.Close()

	body, _ := ioutil.ReadAll(resp.Body)
	fmt.Println(string(body))
}
```
