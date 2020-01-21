export const hexToRGBA = function hexToRGBA(hex, alpha){
    var r = parseInt(hex.slice(1, 3), 16),
        g = parseInt(hex.slice(3, 5), 16),
        b = parseInt(hex.slice(5, 7), 16);

    if(alpha) { return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")"; }
    else { return "rgb(" + r + ", " + g + ", " + b + ")"; }
}

export const hexToAHEX = function hexToAHEX(hex, alpha){
    var a = alpha*255 || 255;

    // console.log(alpha, a, a.toString(16));
    return '#' + a.toString(16) + hex.slice(1, 7);
}

export const hexToHEXA = function hexToHEXA(hex, alpha){
    var a = alpha*255 || 255;

    // console.log(alpha, a, a.toString(16));
    return '#' + hex.slice(1, 7) + a.toString(16);
}

export const RGBToHex = function RGBToHex(rgb) {
    rgb = rgb.match(/^rgba?[\s+]?\([\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?,[\s+]?(\d+)[\s+]?/i);
    return (rgb && rgb.length === 4) ? "#" +
    ("0" + parseInt(rgb[1],10).toString(16)).slice(-2) +
    ("0" + parseInt(rgb[2],10).toString(16)).slice(-2) +
    ("0" + parseInt(rgb[3],10).toString(16)).slice(-2) : '';
}