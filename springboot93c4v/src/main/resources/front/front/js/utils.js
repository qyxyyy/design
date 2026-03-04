/**
 * 页面跳转
 * @param {Object} url
 */
function jump(url) {
	if (!url || url == 'null' || url == null) {
		window.location.href = './index.html';
	}
	// 路径访问设置
	localStorage.setItem('iframeUrl', url.replace('../', './pages/'));
	window.location.href = url;
}

/**
 * 返回
 */
function back(num = -1) {
	window.history.go(num)
}

// 自动在前台页面右下角插入一个“返回上一页”按钮（首页除外）
(function () {
	try {
		// 仅在顶层窗口生效，避免嵌套 iframe 出现多次
		if (window.self !== window.top) return;
		// 首页 index.html 不显示
		var path = window.location.pathname || '';
		if (path.indexOf('index.html') !== -1 || /\/front\/front\/?$/.test(path)) return;

		var btn = document.createElement('button');
		btn.innerText = '返回上一页';
		btn.style.position = 'fixed';
		btn.style.right = '16px';
		btn.style.bottom = '18px';
		btn.style.zIndex = '9999';
		btn.style.padding = '8px 14px';
		btn.style.borderRadius = '18px';
		btn.style.border = 'none';
		btn.style.background = '#2A4365';
		btn.style.color = '#fff';
		btn.style.fontSize = '13px';
		btn.style.boxShadow = '0 2px 8px rgba(0,0,0,.25)';
		btn.style.cursor = 'pointer';
		btn.onclick = function () {
			// 若没有历史记录则回到首页
			if (window.history.length > 1) {
				window.history.back();
			} else {
				window.location.href = './index.html';
			}
		};
		document.addEventListener('DOMContentLoaded', function () {
			document.body.appendChild(btn);
		});
	} catch (e) {
		// 忽略异常，不影响其它逻辑
	}
})();

/**
 * 生成订单
 */
function genTradeNo() {
	var date = new Date();
	var tradeNo = date.getFullYear().toString() + (date.getMonth() + 1).toString() +
		date.getDate().toString() + date.getHours().toString() + date.getMinutes().toString() +
		date.getSeconds().toString() + date.getMilliseconds().toString();
	for (var i = 0; i < 5; i++) //5位随机数，用以加在时间戳后面。
	{
		tradeNo += Math.floor(Math.random() * 10);
	}
	return tradeNo;
}
