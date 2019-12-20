/* eslint-disable no-useless-escape */
const validateFns = {
	link(key, val) {
		const regExp = /^(http(s)?|ftp|tel|mailto|\/)(:\/\/)?/
		return regExp.test(val)
	},
	email(key, val) {
		const regExp = /[-\d\S.+_]+@[-\d\S.+_]+\.[\S]{2,4}/
		return regExp.test(val)
	},
	password(key, val, option) {
		const filteredOption = { start: option[0] || 6, end: option[1] || 100 }
		const regExp = new RegExp(
			`^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&\\(\\)])[A-Za-z\\d$@$!%*#?&\\(\\)]{${filteredOption.start},${filteredOption.end}}$`,
			'i',
		)
		return regExp.test(val)
	},
	name(key, val, option) {
		const filteredOption = { start: option[0] || 2, end: option[1] || 20 }
		const regExp = new RegExp(
			`^([^\\<\\>ㄱ-ㅣ]|[\\w]){${filteredOption.start},${filteredOption.end}}$`,
		)
		return regExp.test(val)
	},
	id(key, val, option) {
		const filteredOption = { start: option[0] || 2, end: option[1] || 20 }
		const regExp = new RegExp(
			`([-_a-z\\d]){${filteredOption.start},${filteredOption.end}}$`,
		)
		return regExp.test(val)
	},
}

class Validator {
	constructor() {
		this.validates = new Map()
		return this
	}

	setup(key, expression) {
		const validates = expression.replace(/'/g, '').split('|')
		const keyword = validates[0]
		const option = validates[1] ? validates[1].split(',') : null
		this.validates.set(key, { keyword, option })
	}

	validate(key, value) {
		const validates = this.validates.get(key)
		if (!validates) return null
		if (Object.prototype.hasOwnProperty.call(validateFns, validates.keyword)) {
			return validateFns[validates.keyword](key, value, validates.option)
		}
	}
}

export default new Validator()
