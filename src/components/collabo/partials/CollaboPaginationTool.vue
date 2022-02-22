<template>
  <nav class="pagination-tool">
    <div class="pagination-tool__container">
      <div class="pagination-tool__link load" @click="prevPage">
        <div class="pagination-tool__link--prev"></div>
      </div>
      <div
        v-for="(page, index) in pages"
        class="pagination-tool__link page"
        :class="{ current: page === curPage }"
        :key="index"
        @click="setCurrent(page)"
      >
        {{ page }}
      </div>

      <div class="pagination-tool__link load" @click="nextPage">
        <div class="pagination-tool__link--next"></div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'CollaboPaginationTool',
  props: {
    totalPage: {
      type: Number,
      required: true,
    },
    currentPage: {
      type: Number,
    },
  },
  data() {
    return {
      curPage: 1,
      pages: [],
      maxPage: 5,

      emitFlag: true,
    }
  },
  watch: {
    currentPage(page) {
      this.emitFlag = false
      this.curPage = page + 1
      if (page === 0) {
        this.curPage = 1
      }
    },
    totalPage(now) {
      this.setPages()
      if (now === 0) {
        this.pages = []
        this.curPage = 1
      }
    },
    curPage(page) {
      this.setPages()
      if (this.emitFlag) {
        this.$emit('current-page', page)
      }
    },
  },
  methods: {
    setCurrent(page) {
      this.emitFlag = true
      this.curPage = page
    },
    setPages() {
      const pagesArray = []

      if (this.curPage === 1) {
        const remained = this.totalPage - this.curPage
        const end = remained < this.maxPage ? remained + 1 : this.maxPage
        for (let i = 1; i <= end; i++) {
          pagesArray.push(i)
        }
      } else if (this.curPage - 1 === this.pages[this.pages.length - 1]) {
        const remained = this.totalPage - this.curPage
        const end = remained < this.maxPage ? remained + 1 : this.maxPage
        for (let i = 0; i < end; i++) {
          pagesArray.push(this.curPage + i)
        }
      } else if (this.curPage === this.pages[0] - 1) {
        for (let i = 4; i >= 0; i--) {
          pagesArray.push(this.curPage - i)
        }
      }

      if (pagesArray.length > 0) {
        this.pages = pagesArray
      }
    },
    prevPage() {
      this.emitFlag = true
      if (this.curPage > 1) {
        this.curPage--
      }
    },
    nextPage() {
      this.emitFlag = true
      if (this.curPage + 1 <= this.totalPage) {
        this.curPage++
      }
    },
  },

  mounted() {
    this.setPages()
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
$color_paging_border: #e9edf4;

.pagination-tool {
  display: flex;

  margin-top: 34px;
  margin-bottom: 80px;
}

.pagination-tool__container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2.7143rem;

  margin: auto;
  background-color: $color_white;
  border: 1px solid #e2e6ee;
  box-shadow: 0px 1px 2px 0px rgba(0, 0, 0, 0.08);
}

.pagination-tool__link {
  position: relative;
  width: 2.7143rem;
  height: 100%;
  color: $color_text_main;
  font-weight: normal;
  font-size: 1rem;
  text-align: center;
  border-radius: 3px;

  &:hover {
    cursor: pointer;
  }

  &.page {
    padding-top: 0.4286rem;
  }

  &.load {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &.current {
    width: 2.2857rem;
    height: 2.2857rem;
    margin: 0.3571rem 0.2143rem 0.3571rem 0.2143rem;
    padding-top: 0.2857rem;
    color: $color_white;
    font-weight: 500;
    background: $color_primary;
  }
}

.pagination-tool__link--prev {
  width: 0;
  height: 0;
  border-top: 0.2929rem solid transparent;
  border-right: 0.5rem solid #9ea0a5;
  border-bottom: 0.2929rem solid transparent;

  &::after {
    position: absolute;
    top: 0.2143rem;
    left: 2.5rem;
    height: 2.1429rem;
    border-right: 1px solid $color_paging_border;
    content: '';
  }
}

.pagination-tool__link--next {
  width: 0;
  height: 0;
  border-top: 0.2929rem solid transparent;
  border-bottom: 0.2929rem solid transparent;
  border-left: 0.5rem solid #9ea0a5;

  &::before {
    position: absolute;
    top: 0.2143rem;
    right: 2.5rem;
    height: 2.1429rem;
    border-right: 1px solid $color_paging_border;
    content: '';
  }
}
</style>
