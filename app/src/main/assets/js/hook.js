
function getElementById(domId) {
    return document.getElementById(domId)
}

function getElements(clazzName) {
    return document.getElementsByClassName(clazzName)
}

function gone(element) {
	element.style.display = "none"
}

function invisible(element) {
	element.style.visibility = "hidden"
}

function xpath(path) {
    return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
}

function querySelector(selector) {
    return document.querySelector(selector)
}

function takeIf(condition, fun) {
	if (condition) fun()
}

function tryBlock(fun) {
	try {
		fun()
	} catch (e) {
		console.log(e)
	}
}
