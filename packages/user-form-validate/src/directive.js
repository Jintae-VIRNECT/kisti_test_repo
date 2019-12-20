import validator from './validator'

export default {
	validate: {
		bind(el, binding) {
			let select = el.querySelector('input') || el
			validator.setup(select.name, binding.expression)
		},

		update(el, binding, vnode) {
			let select = el.querySelector('input') || el
			const key = select.name
			if (!vnode.context[key]) return
			const errorMessage = validator.validate(key, select.value)
			if (errorMessage === undefined) return
			vnode.context.$set(vnode.context.errorBag, key, errorMessage)
		},
	},
}
