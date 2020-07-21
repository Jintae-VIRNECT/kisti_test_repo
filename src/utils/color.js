export const hexToRGBA = function hexToRGBA(hex, alpha) {
  var r = parseInt(hex.slice(1, 3), 16),
    g = parseInt(hex.slice(3, 5), 16),
    b = parseInt(hex.slice(5, 7), 16)

  if (alpha) {
    return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + alpha + ')'
  } else {
    return 'rgb(' + r + ', ' + g + ', ' + b + ')'
  }
}

export const hexToAHEX = function hexToAHEX(hex, alpha) {
  var a = alpha * 255 || 255

  // console.log(alpha, a, a.toString(16));
  return '#' + a.toString(16) + hex.slice(1, 7)
}

export const ahexToHEX = function ahexToHEX(ahex) {
  return ahex.substr(0, 1) + ahex.substr(3)
}

export const hexToHEXA = function hexToHEXA(hex, alpha) {
  var a = alpha * 255 || 255

  // console.log(alpha, a, a.toString(16));
  return '#' + hex.slice(1, 7) + a.toString(16)
}

export const ahexToRGBA = function ahexToRGBA(ahex) {
  var r = parseInt(ahex.slice(3, 5), 16),
    g = parseInt(ahex.slice(5, 7), 16),
    b = parseInt(ahex.slice(7, 9), 16),
    a = parseInt(ahex.slice(1, 3), 16) / 255

  return 'rgba(' + r + ', ' + g + ', ' + b + ', ' + a + ')'
}
