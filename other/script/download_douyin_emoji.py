import os
import requests
from bs4 import BeautifulSoup
import time
from concurrent.futures import ThreadPoolExecutor

def download_image(session, img_url, emoji_name, output_dir, headers):
    """单张图片下载函数，供线程池调用"""
    filename = os.path.basename(img_url)
    img_path = os.path.join(output_dir, filename)

    try:
        # 如果文件已存在则跳过，提高重复执行效率
        if os.path.exists(img_path):
            return f"{emoji_name}={filename}", False

        resp = session.get(img_url, headers=headers, timeout=10)
        resp.raise_for_status()
        with open(img_path, 'wb') as f:
            f.write(resp.content)
        # 稍微控制频率，避免触发极其严格的 IP 封锁
        time.sleep(0.1)
        return f"{emoji_name}={filename}", True
    except Exception as e:
        print(f"下载失败 {emoji_name}: {e}")
        return None, False

def main():
    # 获取脚本所在目录的上一级目录的上一级目录（即项目根目录）
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(os.path.dirname(script_dir))

    url = "https://www.emojiall.com/zh-hans/platform-douyin"
    base_url = "https://www.emojiall.com"
    output_dir = os.path.join(project_root, "other/downloads/emoji/dy")
    mapping_dir = os.path.join(output_dir, "mapping")
    mapping_file = os.path.join(mapping_dir, "mapping.txt")

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    if not os.path.exists(mapping_dir):
        os.makedirs(mapping_dir)

    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8",
        "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
        "Referer": "https://www.emojiall.com/zh-hans/platform-douyin"
    }

    print(f"正在抓取页面: {url}...")
    session = requests.Session()
    try:
        response = session.get(url, headers=headers, timeout=15)
        response.raise_for_status()
    except Exception as e:
        print(f"页面抓取失败: {e}")
        return

    soup = BeautifulSoup(response.text, 'html.parser')
    tasks = []

    # 预解析所有表情任务
    tables = soup.find_all('table')
    for table in tables:
        for row in table.find_all('tr'):
            cols = row.find_all('td')
            if len(cols) >= 2:
                img_tag = cols[0].find('img')
                if img_tag:
                    img_url = img_tag.get('src') or img_tag.get('data-src')
                    if not img_url: continue
                    if not img_url.startswith('http'): img_url = base_url + img_url

                    emoji_name = cols[1].get_text(strip=True)
                    tasks.append((img_url, emoji_name))

    print(f"共发现 {len(tasks)} 个表情，准备开始并行下载...")

    mapping = []
    # 使用线程池并发下载，兼顾效率与反爬
    # 设置 max_workers 为 5-10 之间是比较安全的平衡点
    with ThreadPoolExecutor(max_workers=8) as executor:
        future_to_emoji = {
            executor.submit(download_image, session, t[0], t[1], output_dir, headers): t[1]
            for t in tasks
        }

        for future in future_to_emoji:
            map_str, is_new = future.result()
            if map_str:
                mapping.append(map_str)
                if is_new:
                    print(f"完成: {map_str}")

    if mapping:
        with open(mapping_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(mapping))
        print(f"\n任务结束: 成功保存 {len(mapping)} 个映射关系到 {mapping_file}")
    else:
        print("未发现有效表情数据。")

if __name__ == "__main__":
    main()
