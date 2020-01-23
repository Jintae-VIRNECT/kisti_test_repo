import dayjs from 'dayjs'
export default [
  {
    id: '1',
    processName: "process' Name 1",
    detailProcessName: "detailProcess' Name 1",
    startAt: new Date(),
    endAt: dayjs()
      .add(1, 'hour')
      .add(23, 'minute'),
    reportedAt: dayjs().subtract(1, 'month'),
    status: 'complete',
    auth: 'Jason',
    auths: ['김펭수', '이펭수', '박펭수', '문펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
    workerNum: 6,
    issue: true,
  },
  {
    id: '2',
    processName: "process' Name 2 ",
    detailProcessName: "detailProcess' Name 2",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(4, 'minute'),
    reportedAt: dayjs().subtract(2, 'month'),
    status: 'complete',
    auth: 'Jason',
    auths: ['김펭수', '이펭수', '박펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '3',
    processName: "process' Name 3",
    detailProcessName: "detailProcess' Name 3",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(43, 'minute'),
    reportedAt: dayjs().subtract(3, 'month'),
    status: 'idle',
    auth: 'Jason',
    auths: ['김펭수', '이펭수'],
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '4',
    processName: "process' Name 4",
    detailProcessName: "detailProcess' Name 4",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(13, 'minute'),
    reportedAt: dayjs().subtract(4, 'month'),
    status: 'imcomplete',
    auths: ['김펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '5',
    processName: "process' Name 5",
    detailProcessName: "detailProcess' Name 5",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(33, 'minute'),
    reportedAt: dayjs().subtract(5, 'month'),
    status: 'progress',
    auths: ['제갈펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '작업 이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '6',
    processName: "process' Name 6",
    detailProcessName: "detailProcess' Name 6",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(17, 'minute'),
    reportedAt: dayjs().subtract(6, 'month'),
    status: 'progress',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '7',
    processName: "process' Name 7",
    detailProcessName: "detailProcess' Name 7",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(7, 'minute'),
    reportedAt: dayjs().subtract(7, 'month'),
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: false,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '8',
    processName: "process' Name 8",
    detailProcessName: "detailProcess' Name 8",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(38, 'minute'),
    reportedAt: dayjs().subtract(8, 'month'),
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '9',
    processName: "process' Name 9",
    detailProcessName: "detailProcess' Name 9",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(8, 'minute'),
    reportedAt: dayjs().subtract(9, 'month'),
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '10',
    processName: "process' Name 10",
    detailProcessName: "detailProcess' Name 10",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(1, 'minute'),
    reportedAt: dayjs().subtract(10, 'month'),
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
  {
    id: '11',
    processName: "process' Name 11",
    detailProcessName: "detailProcess' Name 11",
    startAt: new Date(),

    endAt: dayjs()
      .add(1, 'hour')
      .add(50, 'minute'),
    reportedAt: dayjs().subtract(11, 'month'),
    status: 'imcomplete',
    auths: ['김펭수', '이펭수', '박펭수'],
    auth: 'Jason',
    profileImg: require('@/assets/image/profileImg.jpg'),
    numOfDetailProcess: 6,
    processPercent: 90,
    workerNum: 6,
    issue: true,
    type: '이슈',
    // 체결수 = count / total
    count: 2,
    total: 10,
  },
]
