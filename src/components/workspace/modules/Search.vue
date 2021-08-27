<template>
  <div class="search" :class="appendClass" @click="focus">
    <input
      ref="search__input"
      class="search__input"
      type="text"
      :placeholder="placeholder"
      v-model="text"
      @blur="blur"
    />
    <button class="search__input-icon disabled">
      Search
    </button>
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
    appendClass: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      text: '',
      searchedText: '',
    }
  },
  watch: {
    text(val) {
      if (this.text.trim() === '') {
        this.text = ''
      }
      this.$emit('search', val)
    },
  },
  methods: {
    searchClear() {
      this.text = ''
      this.searchedText = ''
    },
    focus() {
      this.$emit('onInputFocus')
      this.$refs['search__input'].focus()
    },
    blur() {
      this.$emit('onInputBlur')
    },
  },
  mounted() {
    this.$eventBus.$on('search:clear', this.searchClear)
  },
  beforeDestroy() {
    this.$eventBus.$off('search:clear', this.searchClear)
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.search {
  position: relative;
  display: flex;
  width: 14.857rem;
  height: 2.571rem;
  background-color: transparent;
  border: solid 1px rgba(#b3b3b3, 0.2);
  border-radius: 1.286rem;
  transition: width 0.25s ease;
  &:focus-within {
    width: 22.428rem;
    border: solid 1px $color_primary;
  }
}
.search__input {
  flex: 1;
  width: 13.286rem;
  height: 100%;
  margin-left: 1.143rem;
  color: #fff;
  font-size: 1rem;
  background-color: transparent;
  border: none;
  transition: width 0.25s ease, opacity 0.25s ease;
  caret-color: $color_primary;
  &::placeholder {
    color: rgba(#fff, 0.4);
  }
}
.search__input-icon {
  flex: 0 0 auto;
  width: 2.285rem;
  height: 1.571rem;
  margin: auto;
  padding-right: 0.714rem;
  background: url(~assets/image/ic_search.svg) 50%/1.571rem no-repeat;
  @include ir;
  &.disabled {
    cursor: default;
    pointer-events: none;
  }
}

@include responsive-mobile {
  .search {
    width: 3.2rem;
    height: 3.2rem;
    margin-right: 0.8rem;
    background-color: $new_color_bg_icon; //@color 적용 필요
    border: none;
    border-radius: 0.6rem;
    &:focus-within {
      width: 100%;
      border: none;
      .search__input {
        flex: 0.9;
        width: 100%;
        margin-left: 1rem;
        opacity: 1;
      }
      .search__input-icon {
        top: 0;
        margin: 0.8rem 0;
      }
    }
  }
  .search__input {
    flex: 0;
    width: 0px;
    margin-left: 0;
    padding: 0;
    opacity: 0;
    @include fontLevel(75);
  }
  .search__input-icon {
    position: absolute;
    top: 0.8rem;
    right: 0.5rem;
    min-width: 22.84px;
    height: 1.6rem;
    background: url(~assets/image/ic_search_16.svg) 50%/1.571rem no-repeat;
    &.disabled {
      cursor: default;
      pointer-events: initial;
    }
  }
}
</style>
