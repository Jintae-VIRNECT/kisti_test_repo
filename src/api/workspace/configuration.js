import http from 'api/gateway'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

export const getConfiguration = async function(data) {
  // const returnVal = await http('GET_CONFIG', data)

  const returnVal = {
    data: {
      // 오디오 설정
      speaker: '스피커',
      mic: '마이크',
      // 영상 설정
      resolution: ['360p', '480p', '720p'],
      // 언어 설정
      language: ['English (US)', '한국어'],
      // 녹화 설정
      recordingTime: [5, 10, 15, 30], // 최대 녹화 시간
      recordingResolution: ['360p', '480p', '720p'], // 녹화 영상 해상도
    },
    code: 200,
    message: 'complete',
  }
  return returnVal
}

export const updateConfiguration = async function(data) {
  //const returnVal = await http('UPDATE_CONFIG', data)

  const returnVal = {
    code: 200,
    message: 'complete',
  }
  return returnVal
}

export const putLanguage = async function(data) {
  //const returnVal = await http('PUT_CONFIG', data)

  console.log(data)
  const returnVal = {
    data: {
      '': '',
    },
    code: 200,
    message: 'complete',
  }
  return returnVal
}
