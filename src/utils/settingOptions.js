export const resolution = [
  {
    value: '360',
    text: '360p',
    resolution: '480X360',
  },
  {
    value: '480',
    text: '480p(VGA)',
    resolution: '640X480',
  },
  {
    value: '720',
    text: '720p(HD)',
    resolution: '1280X720',
  },
  // {
  //   value: '1080',
  //   text: '1080p(FHD)',
  //   resolution: '1920X1080',
  // },
]

/**
 *
 * @param {Number} quality 영상 해상도 정수값
 * @example getResolutionScale(480)
 * @returns {Number} 다운 스케일 배수
 */
export const getResolutionScale = quality => {
  if (quality >= 1080) {
    return 12
  } else if (quality < 1080 && quality >= 720) {
    return 8
  } else if (quality < 720 && quality >= 480) {
    return 6
  } else {
    return 4
  }
}
