import validator from './validator'

export default {
	data() {
		return {
			errorBag: {},
		}
	},

	computed: {
		$errors() {
			const errorBag = this.errorBag || {}

			return {
				has(key) {
					return !!errorBag[key]
				},
				message(key) {
					return errorBag[key]
				},
			}
		},

		$validator() {
			const context = this

			return {
				validateAll() {
					for (const key of validator.validates.keys()) {
						const errors = validator.validate(key, context.form[key])
						context.$set(context.errorBag, key, errors)
					}
				},
			}
		},
	},
}
