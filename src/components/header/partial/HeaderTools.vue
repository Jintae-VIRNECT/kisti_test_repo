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
  display: flex;
  align-items: center;
  padding: 1.1429rem 0.5rem 1.1429rem 1.1429rem;

  > .popover--wrapper {
    > .profile {
      margin-left: 2.1429rem;
      cursor: pointer;
      &:hover {
        opacity: 0.8;
      }
    }
  }
}
</style>
