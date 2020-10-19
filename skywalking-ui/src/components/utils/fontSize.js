let remFontSize = function(res) {
    let clientWidth =
        window.innerWidth ||
        document.documentElement.clientWidth ||
        document.body.clientWidth;
    if (!clientWidth) return;
    let fontSize = 80 * (clientWidth / 1920);
    return res * fontSize;
};

export { remFontSize };