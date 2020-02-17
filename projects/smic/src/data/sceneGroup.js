function getRandomArbitrary() {
  return Math.floor(Math.random() * (100 - 0) + 0)
}

export default {
  tableOption: {
    colSetting: [
      {
        prop: 'index',
        label: '순번',
        width: '100px',
      },
      {
        prop: 'subProcessName',
        label: '세부공정 콘텐츠 이름',
      },
    ],
  },
  tableData: [
    {
      id: '1',
      subProcessName: '자제 절단',
      progress: getRandomArbitrary(),
      user: '전문가 1',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'complete',
      processPercent: 100,
    },
    {
      id: '2',
      subProcessName: '모터펌프 가동',
      progress: getRandomArbitrary(),
      user: '전문가 2',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'progress',
      processPercent: 50,
    },
    {
      id: '3',
      subProcessName: '배터리 조립',
      progress: getRandomArbitrary(),
      user: '전문가 3',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'progress',
      processPercent: 40,
    },
  ],
}
