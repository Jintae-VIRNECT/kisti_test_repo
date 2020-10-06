import Main from 'components/layout/common/Main'
import Login from 'components/layout/pages/Login'
import Terms from 'components/layout/pages/Terms'
import Signup from 'components/layout/pages/Signup'
import User from 'components/layout/pages/User'
import Find from 'components/layout/pages/Find'
import ResetPass from 'components/layout/pages/onpremise/ResetPassword'
import Complete from 'components/layout/pages/Complete'

export default {
	path: '',
	component: Main,
	children: [
		{
			path: '',
			name: 'login',
			component: Login,
		},
		{
			path: '/terms',
			name: 'terms',
			component: Terms,
		},
		{
			path: '/signup',
			name: 'signup',
			component: Signup,
			props: true,
		},
		{
			path: '/user',
			name: 'user',
			component: User,
			props: true,
		},
		{
			path: '/find',
			name: 'find',
			component: Find,
			props: true,
		},
		{
			path: '/reset_password',
			name: 'reset_password',
			component: ResetPass,
			props: true,
		},
		{
			path: '/find/:findCategory',
			name: 'findTab',
			component: Find,
			props: true,
		},
		{
			path: '/complete',
			name: 'complete',
			component: Complete,
		},
	],
}
