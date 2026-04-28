import os
import shutil

def main():
    # 路径配置
    script_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(os.path.dirname(script_dir))

    src_img_dir = os.path.join(project_root, "other/downloads/emoji/dy")
    mapping_file = os.path.join(src_img_dir, "mapping/mapping.txt")
    target_res_dir = os.path.join(project_root, "app/src/main/res/mipmap-xxxhdpi")
    new_mapping_file = os.path.join(src_img_dir, "mapping/processed_mapping.txt")

    if not os.path.exists(mapping_file):
        print(f"找不到映射文件: {mapping_file}")
        return

    if not os.path.exists(target_res_dir):
        os.makedirs(target_res_dir)

    print(f"正在读取映射关系并迁移图片...")

    new_mappings = []
    with open(mapping_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    for index, line in enumerate(lines, start=1):
        line = line.strip()
        if not line or '=' not in line:
            continue

        emoji_name, old_filename = line.split('=', 1)
        src_path = os.path.join(src_img_dir, old_filename)

        if not os.path.exists(src_path):
            print(f"图片不存在，跳过: {src_path}")
            continue

        # 生成新文件名
        new_res_name = f"emoji_dy_{index}"
        new_filename = f"{new_res_name}.png"
        dst_path = os.path.join(target_res_dir, new_filename)

        # 执行复制/重命名
        try:
            shutil.copy2(src_path, dst_path)
            new_mappings.append(f"{emoji_name}={new_res_name}")
            if index % 20 == 0:
                print(f"已处理 {index} 张图片...")
        except Exception as e:
            print(f"处理失败 {old_filename}: {e}")

    # 保存新的映射关系（不带扩展名，方便 Android R.mipmap.xxx 调用）
    with open(new_mapping_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(new_mappings))

    print(f"\n迁移完成！")
    print(f"图片已导出至: {target_res_dir}")
    print(f"新的映射文件已生成: {new_mapping_file}")
    print(f"总计处理图片: {len(new_mappings)} 张")

if __name__ == "__main__":
    main()
