<template>
  <div class="search">
    <input
      class="search__input"
      type="text"
      :placeholder="placeholder"
      v-model="search"
    />
  </div>
</template>

<script>
export default {
  name: 'Search',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      search: '',
    }
  },
  watch: {
    search(sch) {
      console.log(sch)
      this.$emit('search', sch)
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.search {
  position: absolute;
  top: 0;
  right: 0;
  width: fit-content;
  height: 36px;
  padding: 0 36px 0 16px;
  background-color: transparent;
  border: solid 1px rgba(#b3b3b3, 0.2);
  border-radius: 18px;
  &:before {
    position: absolute;
    top: 6px;
    right: 10px;
    width: 22px;
    height: 22px;
    background: url(~assets/image/material_search.png) 50%/22px no-repeat;
    content: '';
  }
  &:focus-within {
    border: solid 1px #0f75f5;
  }
}
.search__input {
  width: 186px;
  height: 100%;
  color: #fff;
  background-color: transparent;
  border: none;
  transition: width 0.25s ease;
  caret-color: #0f75f5;
  &:focus {
    width: 320px;
    transition: width 0.25s ease;
  }
  &::placeholder {
    color: rgba(#fff, 0.4);
  }
}
</style>
