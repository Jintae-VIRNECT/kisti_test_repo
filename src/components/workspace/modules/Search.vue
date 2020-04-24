<template>
  <div class="search" :class="appendClass">
    <input
      class="search__input"
      type="text"
      :placeholder="placeholder"
      v-on:input="search = $event.target.value"
    />
  </div>
</template>

<script>
import { mapActions } from 'vuex'
import * as Regexp from 'utils/regexp'
export default {
  name: 'Search',
  props: {
    placeholder: {
      type: String,
      default: '',
    },
    appendClass: {
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
      sch = Regexp.escapeRegExp(sch)
      this.$emit('search', sch)
      this.setFilter(sch)
    },
  },
  methods: {
    ...mapActions(['setFilter']),
  },

  /* Lifecycles */
  mounted() {
    this.setFilter(this.search)
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.search {
  position: relative;
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
    background: url(~assets/image/ic_search.svg) 50%/22px no-repeat;
    content: '';
  }
  &:focus-within {
    border: solid 1px $color_primary;
  }
}
.search__input {
  width: 186px;
  height: 100%;
  color: #fff;
  background-color: transparent;
  border: none;
  transition: width 0.25s ease;
  caret-color: $color_primary;
  &:focus {
    width: 320px;
    transition: width 0.25s ease;
  }
  &::placeholder {
    color: rgba(#fff, 0.4);
  }
}
</style>
