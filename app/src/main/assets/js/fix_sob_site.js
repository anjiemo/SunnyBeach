
// 隐藏 header
tryBlock(() => {
    var element = getElementById("header-container")
    gone(element)
})

// 隐藏文章详情界面相关
tryBlock(() => {
    var element = getElementById("article-detail-left-part")
    gone(element)
})

tryBlock(() => {
    var element = getElementById("article-detail-right-part")
    gone(element)
})

tryBlock(() => {
    var element = getElementById("article-detail-center-part")
    element.style.margin='-20px 0 0 0';
})

// 隐藏问答详情界面相关
tryBlock(() => {
    var element = getElementById("wenda-detail-left-part")
    gone(element)
})

tryBlock(() => {
    var element = getElementById("wenda-detail-right-part")
    gone(element)
})

tryBlock(() => {
    var element = getElementById("wenda-detail-center-part")
    element.style.margin='-20px 0 0 0';
})

// 隐藏 footer
tryBlock(() => {
    var element = getElementById("footer-container")
    gone(element)
})

// 设置整体界面的宽度
tryBlock(() => {
    var element = getElementById("main-content")
    element.style.width='750px';
})

