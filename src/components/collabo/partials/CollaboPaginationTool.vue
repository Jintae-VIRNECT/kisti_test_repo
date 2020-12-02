<template>
  <nav class="pagination-tool">
    <div class="pagination-tool__container">
      <div class="pagination-tool__link prev" @click="prevPage"></div>
      <div
        v-for="(page, index) in pages"
        class="pagination-tool__link page"
        :class="{ current: page === curPage }"
        :key="index"
        @click="setCurrrent(page)"
      >
        {{ page }}
      </div>

      <div class="pagination-tool__link next" @click="nextPage"></div>
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
    }
  },
  watch: {
    currentPage(page) {
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
      this.$emit('current-page', page)
    },
  },
  methods: {
    setCurrrent(page) {
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
      if (this.curPage > 1) {
        this.curPage--
      }
    },
    nextPage() {
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
  background-color: #ffffff;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px #eaedf3;
}

.pagination-tool__link {
  position: relative;
  width: 2.7143rem;
  height: 100%;
  color: #0b1f48;
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

  &.prev {
    width: 2.7857rem;
    padding: 0.4286rem 1.0714rem;
    background: url(~assets/image/ic_arrow_left.svg) center/60% no-repeat;
    transform: rotate(180deg);
  }

  &.prev::after {
    position: absolute;
    top: 0.1429rem;
    left: 0.1429rem;
    height: 2.1429rem;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.next {
    width: 2.7857rem;
    padding: 0.4286rem 1.0714rem;
    background: url(~assets/image/ic_arrow_right.svg) center/60% no-repeat;
  }

  &.next::before {
    position: absolute;
    top: 0.2143rem;
    right: 2.5714rem;
    height: 2.1429rem;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.current {
    width: 2.2857rem;
    height: 2.2857rem;
    margin: 0.3571rem 0.2143rem 0.3571rem 0.2143rem;
    padding-top: 0.2857rem;
    color: #ffffff;
    font-weight: 500;
    background: #1665d8;
  }
}
</style>
