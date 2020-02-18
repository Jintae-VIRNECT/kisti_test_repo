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
        prop: 'name',
        label: '세부공정 콘텐츠 이름',
      },
    ],
  },
  tableData: [
    {
      id: '1',
      name: '작업 1',
      progress: getRandomArbitrary(),
      user: '전문가 1',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'complete',
      processPercent: 100,
      count: 3,
      total: 10,
      issueId: 5,
      reportId: 6,
    },
    {
      id: '2',
      name: '작업 2',
      progress: getRandomArbitrary(),
      user: '전문가 2',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'progress',
      processPercent: 50,
      count: 3,
      total: 10,
      issueId: 3,
      reportId: false,
      smartTool: 9,
    },
    {
      id: '3',
      name: '작업 3',
      progress: getRandomArbitrary(),
      user: '전문가 3',
      auths: ['전문가 1', '전문가 2', '전문가 3'],
      startAt: '2020.02.03 14:00',
      numOfDetailProcess: 3,
      endAt: '2020.02.03 16:00',
      issue: true,
      status: 'progress',
      processPercent: 40,
      count: 7,
      total: 10,
      issueId: false,
      reportId: 2,
    },
  ],
}
