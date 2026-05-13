window.addEventListener(
    "mousewheel",
    function (event) {
        if (event.ctrlKey === true || event.metaKey) {
            event.preventDefault();
        }
    },
    { passive: false },
);

window.addEventListener(
    "DOMMouseScroll",
    function (event) {
        if (event.ctrlKey === true || event.metaKey) {
            event.preventDefault();
        }
    },
    { passive: false },
);

var fileInfoData = null;
var currentThemeJson = "";
var currentVideoItem = null;
var currentJsonText = "";
var copyToastTimer = null;

function applyTheme(theme) {
    var json = JSON.stringify(theme);
    if (json === currentThemeJson) return;
    currentThemeJson = json;
    document.documentElement.style.setProperty("--border-color", theme.borderColor);
    document.documentElement.style.setProperty("--background-color", theme.backgroundColor);
    document.documentElement.style.setProperty("--font-color", theme.fontColor);
    document.documentElement.style.setProperty("--font-family", theme.fontFamily);
    document.documentElement.style.setProperty("--font-family-mono", theme.fontFamilyMono);
    document.documentElement.style.setProperty("--tab-active-bg", theme.tabActiveBg);
    document.documentElement.style.setProperty("--scrollbar-thumb-color", theme.scrollbarThumbColor);
    document.documentElement.style.setProperty("--scrollbar-thumb-hover-color", theme.scrollbarThumbHoverColor);
    document.documentElement.style.setProperty("--row-alt-bg", theme.rowAltBg);
    switchHljsTheme(theme.backgroundColor);
    updateHljsBgColor();
}

function updateHljsBgColor() {
    var el = document.querySelector("#materialJson .hljs");
    if (!el) return;
    var bg = getComputedStyle(el).backgroundColor;
    document.documentElement.style.setProperty("--hljs-bg", bg);
}

function switchHljsTheme(bgColor) {
    var link = document.getElementById("hljs-theme");
    if (!link) return;
    var rgb = bgColor.match(/\d+/g);
    var brightness = rgb ? (parseInt(rgb[0]) + parseInt(rgb[1]) + parseInt(rgb[2])) / 3 : 0;
    var isDark = brightness < 128;
    var target = isDark ? "css/hljs-dark.min.css" : "css/hljs-light.min.css";
    if (link.getAttribute("href") === target) return;
    var newLink = document.createElement("link");
    newLink.id = "hljs-theme";
    newLink.rel = "stylesheet";
    newLink.href = target;
    newLink.onload = function () {
        if (link.parentNode) link.parentNode.removeChild(link);
        updateHljsBgColor();
    };
    link.parentNode.insertBefore(newLink, link.nextSibling);
}

function fetchFileInfo() {
    return fetch("/file-info.json")
        .then(function (r) {
            return r.json();
        })
        .then(function (info) {
            fileInfoData = info;
        })
        .catch(function (e) {
            console.error("Failed to fetch file info:", e);
        });
}

window.onThemeUpdate = function (theme) {
    applyTheme(theme);
};

function onPageLoaded() {
    fetchFileInfo().then(function () {
        var player = new SVGA.Player("#playerCanvas");
        var parser = new SVGA.Parser("#playerCanvas");
        parser.load("/file.svga", function (videoItem) {
            currentVideoItem = videoItem;
            var canvas = document.getElementById("playerCanvas");
            canvas.style.width = videoItem.videoSize.width + "px";
            canvas.style.height = videoItem.videoSize.height + "px";
            player.setVideoItem(videoItem);
            player.startAnimation();
            processSvgaInfo(videoItem);
            initMaterialView(videoItem);
        });
    });
}

function onSwitchBackground(target) {
    document.getElementById("playerCanvas").style.backgroundImage = getComputedStyle(target, null).backgroundImage;
    document.getElementById("playerCanvas").style.backgroundColor = getComputedStyle(target, null).backgroundColor;
    document.getElementById("materialPreviewInner").style.backgroundImage = getComputedStyle(
        target,
        null,
    ).backgroundImage;
    document.getElementById("materialPreviewInner").style.backgroundColor = getComputedStyle(
        target,
        null,
    ).backgroundColor;
    if (target.id === "switch-bg-none") {
        document.getElementById("playerCanvas").style.borderWidth = "1px";
        document.getElementById("materialPreviewInner").style.borderWidth = "1px";
    } else {
        document.getElementById("playerCanvas").style.borderWidth = "0";
        document.getElementById("materialPreviewInner").style.borderWidth = "0";
    }
}

function onSwitchTab(tabName) {
    var tabPlayer = document.getElementById("tabPlayer");
    var tabMaterial = document.getElementById("tabMaterial");
    var playerPanel = document.getElementById("playerPanel");
    var materialPanel = document.getElementById("materialPanel");

    if (tabName === "player") {
        tabPlayer.className = "tab-btn tab-active";
        tabMaterial.className = "tab-btn";
        playerPanel.className = "panel active";
        materialPanel.className = "panel";
    } else {
        tabPlayer.className = "tab-btn";
        tabMaterial.className = "tab-btn tab-active";
        playerPanel.className = "panel";
        materialPanel.className = "panel active";
    }
}

function initMaterialView(videoItem) {
    var materialMemoryBytes = 0;
    var listEl = document.getElementById("imageKeyList");
    var jsonEl = document.getElementById("jsonDisplay");
    var keys = Object.keys(videoItem.images);
    var sprites = Object.keys(videoItem.sprites);
    var audios = Object.keys(videoItem.audios);
    var fileSizeB = fileInfoData ? fileInfoData.fileSizeB : 0;
    listEl.innerHTML = "";

    if (keys.length === 0) {
        document.getElementById("materialPreviewImg").removeAttribute("src");
        document.getElementById("materialPreviewInner").style.display = "none";
        document.getElementById("materialPreviewEmpty").style.display = "flex";
        document.getElementById("imageKeyList").style.display = "none";
        document.getElementById("imageKeyListEmpty").style.display = "flex";
    } else {
        document.getElementById("materialPreviewInner").style.display = "";
        document.getElementById("materialPreviewEmpty").style.display = "none";
        document.getElementById("imageKeyList").style.display = "";
        document.getElementById("imageKeyListEmpty").style.display = "none";

        var isFirst = true;
        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            var base64 = videoItem.images[key];
            var size = getImageSizeFromBase64Data(base64);
            materialMemoryBytes += size.width * size.height * 4;

            var li = document.createElement("li");
            li.setAttribute("data-imageid", key);
            var indexSpan = document.createElement("span");
            indexSpan.className = "image-key-index";
            indexSpan.textContent = i;
            var keySpan = document.createElement("span");
            keySpan.className = "image-key-name";
            keySpan.textContent = key;
            var sizeSpan = document.createElement("span");
            sizeSpan.className = "image-key-size";
            sizeSpan.textContent = size.width + "x" + size.height;
            li.appendChild(indexSpan);
            li.appendChild(keySpan);
            li.appendChild(sizeSpan);
            if (isFirst) {
                li.className = "is-active";
                showMaterialPreview(base64, size);
                isFirst = false;
            }
            li.addEventListener("click", onImageKeyClick);
            listEl.appendChild(li);
        }
    }

    var metadata = {
        version: videoItem.version,
        fps: videoItem.FPS,
        fileSize: fileSizeB,
        memory: materialMemoryBytes,
        frames: videoItem.frames,
        images: keys.length,
        sprites: sprites.length,
        audios: audios.length,
        videoSize: videoItem.videoSize,
    };
    currentJsonText = JSON.stringify(metadata, null, 2);
    jsonEl.textContent = currentJsonText;
    hljs.highlightElement(jsonEl);
    appendCopyButton(jsonEl);
    updateHljsBgColor();
}

function onImageKeyClick(e) {
    var target = e.currentTarget;
    var imageId = target.getAttribute("data-imageid");
    if (!currentVideoItem || !currentVideoItem.images[imageId]) return;

    var base64 = currentVideoItem.images[imageId];
    var size = getImageSizeFromBase64Data(base64);
    showMaterialPreview(base64, size);

    var items = document.getElementById("imageKeyList").getElementsByTagName("li");
    for (var i = 0; i < items.length; i++) {
        items[i].className = "";
    }
    target.className = "is-active";
}

function showMaterialPreview(base64, size) {
    var innerEl = document.getElementById("materialPreviewInner");
    var imgEl = document.getElementById("materialPreviewImg");
    innerEl.style.width = size.width + "px";
    innerEl.style.height = size.height + "px";
    imgEl.src = "data:image/png;base64," + base64;
}

function processSvgaInfo(videoItem) {
    var bc = 0;
    for (var key in videoItem.images) {
        if (videoItem.images.hasOwnProperty(key)) {
            var n = getImageSizeFromBase64Data(videoItem.images[key]);
            bc += n.width * n.height * 4;
        }
    }
    var fileSize = fileInfoData && fileInfoData.fileSize ? fileInfoData.fileSize : "";
    var imageCount = currentVideoItem && currentVideoItem.images ? Object.keys(currentVideoItem.images).length : 0;
    document.getElementById("infoDiv").innerHTML =
        videoItem.videoSize.width +
        "x" +
        videoItem.videoSize.height +
        "\xa0\xa0SVGA/" +
        videoItem.version +
        "\xa0\xa0FPS:\xa0" +
        videoItem.FPS +
        "\xa0\xa0Frames:\xa0" +
        videoItem.frames +
        "\xa0\xa0Images: " +
        imageCount +
        "\xa0\xa0Memory: " +
        processFileSizeText(bc) +
        "\xa0\xa0File: " +
        fileSize;
}

function getImageSizeFromBase64Data(base64) {
    var dec = window.atob(base64),
        length = dec.length,
        array = new Uint8Array(new ArrayBuffer(length)),
        i;
    for (i = 0; i < length; i++) array[i] = dec.charCodeAt(i);
    return { width: 256 * array[18] + array[19], height: 256 * array[22] + array[23] };
}

function processFileSizeText(bc) {
    if (bc < 1024) {
        return bc + "B";
    } else if (bc < 1048576) {
        return Math.round(((bc * 1.0) / 1024) * 10) / 10.0 + "K";
    } else {
        return Math.round(((bc * 1.0) / 1048576) * 100) / 100.0 + "M";
    }
}

function onCopyJson() {
    if (!currentJsonText) return;
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(currentJsonText).then(showCopyToast);
    } else {
        var ta = document.createElement("textarea");
        ta.value = currentJsonText;
        ta.style.position = "fixed";
        ta.style.left = "-9999px";
        document.body.appendChild(ta);
        ta.select();
        document.execCommand("copy");
        document.body.removeChild(ta);
        showCopyToast();
    }
}

function appendCopyButton(codeEl) {
    var existing = document.getElementById("copyJsonBtn");
    if (existing) existing.remove();
    var btn = document.createElement("button");
    btn.id = "copyJsonBtn";
    btn.title = "Copy JSON";
    btn.textContent = "Copy";
    btn.onclick = onCopyJson;
    codeEl.insertBefore(btn, codeEl.firstChild);
}

function showCopyToast() {
    var toast = document.getElementById("copyJsonToast");
    if (!toast) return;
    if (copyToastTimer) clearTimeout(copyToastTimer);
    toast.classList.add("visible");
    copyToastTimer = setTimeout(function () {
        toast.classList.remove("visible");
        copyToastTimer = null;
    }, 2000);
}
