export default ({ app }, inject) => {
  app.alert = type => {
    console.log(type)
  }
}
