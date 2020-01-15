
import { mapGetters } from 'vuex'

export default {
	computed: {
		...mapGetters(['searchSort', 'searchFilter']),
	},
    methods: {
		sortOld(arr, time) {
			let sortArray = arr.slice()
			return sortArray.sort((a, b) => {
				var nameA = a[time]
				var nameB = b[time]
				if (nameA < nameB) {
					return -1;
				}
				if (nameA > nameB) {
					return 1;
				}
	
				return 0
			})
		},
		sortNew(arr, time) {
			let sortArray = arr.slice()
			return sortArray.sort((a, b) => {
				var nameA = a[time]
				var nameB = b[time]
				if (nameA < nameB) {
					return 1;
				}
				if (nameA > nameB) {
					return -1;
				}

				return 0
			})
		},
		sortText(arr, name) {
			let sortArray = arr.slice()
			return sortArray.sort((a, b) => {
				var nameA = a[name].toUpperCase(); // ignore upper and lowercase
				var nameB = b[name].toUpperCase(); // ignore upper and lowercase
				if (nameA < nameB) {
					return -1;
				}
				if (nameA > nameB) {
					return 1;
				}

				return 0
			})
		},
		sortTextReverse(arr, name) {
			let sortArray = arr.slice()
			return sortArray.sort((a, b) => {
				var nameA = a[name].toUpperCase(); // ignore upper and lowercase
				var nameB = b[name].toUpperCase(); // ignore upper and lowercase
				if (nameA < nameB) {
					return 1;
				}
				if (nameA > nameB) {
					return -1;
				}

				return 0
			})
		}
	},
	mounted() {
		this.$eventBus.$emit('clear')
	}
}