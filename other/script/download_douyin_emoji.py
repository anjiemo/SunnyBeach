import os
import requests
from bs4 import BeautifulSoup
import time

def main():
    url = "https://www.emojiall.com/zh-hans/platform-douyin"
    base_url = "https://www.emojiall.com"
    output_dir = "other/downloads/emoji/dy"
    mapping_file = os.path.join(output_dir, "mapping.txt")

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36"
    }

    print(f"Fetching {url}...")
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
    except Exception as e:
        print(f"Error fetching URL: {e}")
        return

    soup = BeautifulSoup(response.text, 'html.parser')
    mapping = []

    # Find all tables with emojis
    tables = soup.find_all('table')
    print(f"Found {len(tables)} tables.")

    for i, table in enumerate(tables):
        rows = table.find_all('tr')
        print(f"Processing table {i+1} with {len(rows)} rows...")
        for row in rows:
            cols = row.find_all('td')
            if len(cols) >= 2:
                img_tag = cols[0].find('img')
                if img_tag:
                    img_url = img_tag.get('src')
                    if not img_url:
                        img_url = img_tag.get('data-src')

                    if not img_url:
                        continue

                    if not img_url.startswith('http'):
                        img_url = base_url + img_url

                    emoji_name = cols[1].get_text(strip=True)

                    # Extract filename from URL
                    filename = os.path.basename(img_url)

                    # Download image
                    img_path = os.path.join(output_dir, filename)
                    try:
                        # Skip if already exists to save time/bandwidth
                        if not os.path.exists(img_path):
                            img_data = requests.get(img_url, headers=headers).content
                            with open(img_path, 'wb') as f:
                                f.write(img_data)
                            print(f"Downloaded {emoji_name} -> {filename}")
                            time.sleep(0.05) # Be nice
                        else:
                            print(f"Skipping {emoji_name} (already exists)")

                        mapping.append(f"{emoji_name}={filename}")
                    except Exception as e:
                        print(f"Failed to download {emoji_name}: {e}")

    if mapping:
        with open(mapping_file, 'w', encoding='utf-8') as f:
            f.write('\n'.join(mapping))
        print(f"Successfully saved {len(mapping)} mappings to {mapping_file}")
    else:
        print("No emojis found to map.")

if __name__ == "__main__":
    main()
