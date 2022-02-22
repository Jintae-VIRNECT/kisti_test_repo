export default {
  methods: {
    alertMessage(title, message, type) {
      const h = this.$createElement
      this.$message({
        showClose: true,
        message: h('p', null, [h('h3', null, title), h('p', null, message)]),
        type: type,
        // duration: 0
      })
    },
    async confirmWindow(title, msg, okBtn) {
      try {
        await this.$alert(msg, title, {
          confirmButtonText: okBtn,
        })
        location.replace('/')
      } catch (e) {
        console.log(e)
      }
    },
    passValidate(password) {
      let typeCount = 0
      if (/[0-9]/.test(password)) typeCount++
      if (/[a-z]/.test(password)) typeCount++
      if (/[A-Z]/.test(password)) typeCount++
      if (/[$.$!$@$#$$$%]/.test(password)) typeCount++
      if (typeCount < 3) return false
      if (!/^.{8,20}$/.test(password)) return false
      if (/(.)\1\1\1/.test(password)) return false
      if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password))
        return false
      if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password))
        return false
      if (!/^[0-9a-zA-Z$.$!$@$#$$$%]+$/.test(password)) return false
      return true
    },
  },
}
