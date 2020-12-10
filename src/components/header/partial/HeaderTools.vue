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
  padding: 1.1429rem 0.5rem 1.1429rem 1.1429rem;

  > .tooltip,
  > .popover--wrapper {
    > .language-button {
      // margin-top: 10px;
    }
    > .profile {
      margin: 0rem 0.143rem 0.143rem 0.929rem;
      margin-left: 2.1429rem;
      cursor: pointer;
      &:hover {
        opacity: 0.8;
      }
    }
  }
}
</style>
