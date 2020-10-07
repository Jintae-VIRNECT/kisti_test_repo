<template>
  <nav class="pagination-tool">
    <div class="pagination-tool__container">
      <div class="pagination-tool__link prev" @click="prevPage"></div>
      <div
        v-for="(page, index) in pages"
        class="pagination-tool__link page"
        :class="{ current: page === currentPage }"
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
  props: {
    totalPage: {
      type: Number,
      required: true,
    },
  },

  data() {
    return {
      currentPage: 1,
      pages: [],
      maxPage: 5,
    }
  },

  computed: {},
  watch: {
    currentPage(pageNum) {
      this.setPages()
      this.$emit('currentPage', pageNum)
    },
  },
  methods: {
    setCurrrent(page) {
      this.currentPage = page
    },
    setPages() {
      const pagesArray = []

      if (this.currentPage === 1) {
        const remained = this.totalPage - this.currentPage
        const end = remained <= this.maxPage ? remained + 1 : this.maxPage
        for (let i = 1; i <= end; i++) {
          pagesArray.push(i)
        }
      } else if (this.currentPage - 1 === this.pages[this.pages.length - 1]) {
        const remained = this.totalPage - this.currentPage
        const end = remained < this.maxPage ? remained + 1 : this.maxPage
        for (let i = 0; i < end; i++) {
          pagesArray.push(this.currentPage + i)
        }
      } else if (this.currentPage === this.pages[0] - 1) {
        for (let i = 4; i >= 0; i--) {
          pagesArray.push(this.currentPage - i)
        }
      }

      if (pagesArray.length > 0) {
        this.pages = pagesArray
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
      }
    },
    nextPage() {
      if (this.currentPage + 1 <= this.totalPage) {
        this.currentPage++
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
  margin: auto;
  margin-top: 3.0714rem;
}

.pagination-tool__container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 2.7143rem;

  margin: auto;
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
    background: url(~@/assets/image/ic_arrow_left.svg) center/60% no-repeat;
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
    background: url(~@/assets/image/ic_arrow_right.svg) center/60% no-repeat;
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
