import dayjs from 'dayjs'
export default [
  {
    processId: '1',
    processName: '전기오토바이 제조',
    reportedAt: dayjs('2010-02-10')
      .add(0, 'hour')
      .minute(0),
    status: 'progress',
    auth: 'Jason',
    processPercent: '30',
    workerNum: 6,
  },
  {
    processId: '2',
    processName: '터빈 조립',
    reportedAt: dayjs('2010-02-11')
      .add(1, 'hour')
      .minute(0),
    status: 'progress',
    auth: 'Jason',
    processPercent: '50',
    workerNum: 6,
  },
  {
    processId: '3',
    processName: '모터 펌프 조립',
    reportedAt: dayjs('2010-02-12')
      .add(2, 'hour')
      .minute(0),
    status: 'complete',
    auth: 'Jason',
    processPercent: '100',
    workerNum: 6,
  },
  {
    processId: '4',
    processName: '배기관 조립',
    reportedAt: dayjs('2010-02-20')
      .add(3, 'hour')
      .minute(0),
    status: 'progress',
    auth: 'Jason',
    processPercent: '70',
    workerNum: 6,
  },
  {
    processId: '5',
    processName: '계기판 조립',
    reportedAt: dayjs('2010-02-08')
      .add(4, 'hour')
      .minute(0),
    status: 'progress',
    auth: 'Jason',
    processPercent: '50',
    workerNum: 6,
  },
  {
    processId: '6',
    processName: '전기 배선 연결',
    reportedAt: dayjs('2010-02-15')
      .add(5, 'hour')
      .minute(0),
    status: 'complete',
    auth: 'Jason',
    processPercent: '100',
    workerNum: 6,
  },
  {
    processId: '7',
    processName: '게이지 조립',
    reportedAt: dayjs('2010-02-13')
      .add(6, 'hour')
      .minute(0),
    status: 'progress',
    auth: 'Jason',
    processPercent: '99',
    workerNum: 6,
  },
]
