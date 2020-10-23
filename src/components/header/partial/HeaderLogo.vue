<template>
  <div class="header-logo">
    <img class="header-logo--image" src="~assets/image/logo_symtext.svg" />
    <div class="header-logo--text"></div>
  </div>
</template>

<script>
export default {
  name: 'HeaderLogo',
}
</script>

<style lang="scss">
.header-logo {
  display: flex;
  padding: 1.1429rem 0rem;
}

.header-logo--image {
  width: 9.5136rem;
  height: 2.2286rem;
  margin-right: 0.8571rem;
  border-radius: 0rem;
}

.header-logo--text {
  width: 7.5714rem;
  height: 1.8571rem;
  margin-top: 0.2143rem;
  font-weight: 500;
  font-size: 1.0286rem;
  text-align: center;
  background: rgb(31, 127, 250) url('~assets/image/logo_sub_text.svg');
  border-radius: 0.1714rem;
}
</style>
