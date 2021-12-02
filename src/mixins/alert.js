export const alertMessage = (root, title, message, type) => {
  const h = root.$createElement
  root.$message({
    showClose: true,
    message: h('p', null, [h('h3', null, title), h('p', null, message)]),
    type: type,
    // duration: 0
  })
}

export const confirmWindow = async (root, title, msg, okBtn) => {
  try {
    await root.$alert(msg, title, {
      confirmButtonText: okBtn,
    })
    location.replace('/')
  } catch (e) {
    console.log(e)
  }
}
