<template>
  <div class="header-tools">
    <header-language></header-language>
    <header-profile></header-profile>
  </div>
</template>

<script>
import HeaderLanguage from './HeaderLanguage'
import HeaderProfile from './HeaderProfile'
export default {
  name: 'HeaderTools',
  components: {
    HeaderLanguage,
    HeaderProfile,
  },
}
</script>

<style lang="scss">
.header-tools {
  position: absolute;
  right: 22.8571rem;
  display: flex;
  padding: 16px;

  > .tooltip,
  > .popover--wrapper {
    margin-left: 30px;
    > .profile {
      margin: 0px 2.002px 2.002px 13.006px;
      cursor: pointer;
      &:hover {
        opacity: 0.8;
      }
    }
  }
}
</style>
