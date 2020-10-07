<template>
  <nav class="header-workspace-lnb">
    <lnb-selector></lnb-selector>
  </nav>
</template>

<script>
import LnbSelector from '../tools/LnbSelector'
export default {
  name: 'HeaderWorkspaceNav',
  components: {
    LnbSelector,
  },
  methods: {},
}
</script>

<style lang="scss">
.header-workspace-lnb {
  position: relative;
  width: 540px;
  height: 40px;
  margin: auto 0;
  text-align: left;
  background: rgb(69, 69, 69);
  border-radius: 4px;
  .popover--wrapper {
    height: 100%;
  }
  .header-workspace-selector {
    position: relative;
    width: 540px;
    height: 100%;
    padding: 0 1.786rem 0 1.429rem;
    color: #dcdcdc;
    font-weight: normal;
    font-size: 15px;
    text-align: left;
    background: transparent;

    &::after {
      position: absolute;
      top: 9px;
      right: 12px;
      width: 24px;
      height: 24px;
      background: url(~assets/image/ic_dropdown.svg) 50% no-repeat;
      content: '';
    }
  }
}

// .header-workspace-lnb {
//   &::after {
//     position: absolute;
//     top: 9px;
//     right: 12px;
//     width: 24px;
//     height: 24px;
//     background: url(~assets/image/ic_dropdown.svg) 50% no-repeat;
//     content: '';
//   }
// }
</style>
