import http from 'api/gateway'
import dummyJsonSetting from './ws_setting.json'
/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */

export const getConfiguration = async function(param) {
  // const returnVal = await http('GET_CONFIG', param)
  const returnVal = dummyJsonSetting
  return returnVal
}

export const updateConfiguration = async function(param) {
  //const returnVal = await http('UPDATE_CONFIG', param)
  console.log('updateConfiguration::', param)
  const returnVal = {
    code: 200,
    message: 'complete',
  }
  return returnVal
}

export const putLanguage = async function(param) {
  //const returnVal = await http('PUT_CONFIG', param)

  const returnVal = {
    data: {
      '': '',
    },
    code: 200,
    message: 'complete',
  }
  return returnVal
}
