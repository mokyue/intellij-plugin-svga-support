function onPageLoaded() {
    var player = new SVGA.Player('#playerCanvas');
    var parser = new SVGA.Parser('#playerCanvas');
    parser.load('{SVGA_DATA_STUFF}', function (videoItem) {
            document.getElementById('playerCanvas').style.width = "".concat(videoItem.videoSize.width, "px");
            document.getElementById('playerCanvas').style.height = "".concat(videoItem.videoSize.height, "px");
            player.setVideoItem(videoItem);
            player.startAnimation();
        }
    );
}
