import http from 'api/gateway'

/**
 * ----------------------------------------------------------------
 * | !!!!!Warning! these functions return mocking response!!!!!!! |
 * ----------------------------------------------------------------
 */
export const getHistoryList = async function() {
  //const returnVal = await http('GET_HISTORY_LIST')
  const returnVal = {
    data: {
      totalCount: '20',
      currentCount: '10',
      romms: [
        {
          roomId: '1',
          title: '더미1',
          description: '더미 1',
          memberCount: '3',
          totalUseTime: 'xxxxx',
          collaborationStartDate: '2020-xx-xx',
          member: [
            {
              participantId: '1',
              participantRole: 'Master',
              participantName: 'aaa',
              path: 'No',
            },
            {
              participantId: '2',
              participantRole: '',
              participantName: 'sss',
              path: 'No',
            },

            {
              participantId: '3',
              participantRole: 'Manager',
              participantName: 'ddd ddd',
              path: 'No',
            },
          ],
        },
        {
          roomId: '2',
          title: '더미2',
          description: '더미 2',
          memberCount: '2',
          totalUseTime: 'xxxxx',
          collaborationStartDate: '2020-xx-xx',
          member: [
            {
              participantId: '1',
              participantRole: 'Master',
              participantName: 'fff',
              path: 'No',
            },
            {
              participantId: '2',
              participantRole: '',
              participantName: 'hhhh',
              path: 'No',
            },
          ],
        },
      ],
    },
    code: 200,
    message: 'complete',
  }
  return returnVal
}
