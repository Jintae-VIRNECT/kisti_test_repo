export default {
	tableOption: {
		title: '최근 업로드된 콘텐츠',
		moreHref: '/',
		colSetting: [
			{
				prop: 'contentId',
				label: '콘텐츠 ID',
			},
			{
				prop: 'contentName',
				label: '콘텐츠 이름',
			},
			{
				prop: 'volume',
				label: '크기',
			},
			{
				prop: 'uploadedAt',
				label: '업로드일',
			},
			{
				prop: 'auth',
				label: '업로드 멤버',
			},
		],
	},
	tableData: [
		{
			contentId: '2016-05-03',
			contentName: 'Tom',
			uploadedAt: '2020.02.22',
			auth: 'Jason',
			profile_img: '',
			volume: '20',
		},
		{
			contentId: '2016-05-02',
			contentName: 'Tom',
			uploadedAt: '2020.02.22',
			auth: 'Jason',
			profile_img: '',
			volume: '20',
		},
		{
			contentId: '2016-05-04',
			contentName: 'Tom',
			uploadedAt: '2020.02.22',
			auth: 'Jason',
			profile_img: '',
			volume: '20',
		},
		{
			contentId: '2016-05-01',
			contentName: 'Tom',
			uploadedAt: '2020.02.22',
			auth: 'Jason',
			profile_img: '',
			volume: '20',
		},
	],
}
