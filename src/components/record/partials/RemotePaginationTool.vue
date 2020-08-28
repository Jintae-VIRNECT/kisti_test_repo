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
  margin-top: 43px;
}

.pagination-tool__container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 38px;

  margin: auto;
  border: 1px solid #eaedf3;
  box-shadow: 0px 1px 0px 0px rgb(234, 237, 243);
}

.pagination-tool__link {
  position: relative;
  width: 38px;
  height: 100%;
  color: rgb(11, 31, 72);
  font-weight: normal;
  font-size: 14px;
  text-align: center;
  border-radius: 3px;

  &:hover {
    cursor: pointer;
  }

  &.page {
    padding-top: 6px;
  }

  &.prev {
    width: 39px;
    padding: 6px 15px;
    background: url(~@/assets/img/ic_arrow_left.svg) center/60% no-repeat;
    transform: rotate(180deg);
  }

  &.prev::after {
    position: absolute;
    top: 2px;
    left: 2px;
    height: 30px;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.next {
    width: 39px;
    padding: 6px 15px;
    background: url(~@/assets/img/ic_arrow_right.svg) center/60% no-repeat;
  }

  &.next::before {
    position: absolute;
    top: 3px;
    right: 36px;
    height: 30px;
    border-right: 1px solid #e9edf4;
    content: '';
  }

  &.current {
    width: 32px;
    height: 32px;
    margin: 5px 3px 5px 3px;
    padding-top: 4px;
    color: #ffffff;
    font-weight: 500;
    background: #1665d8;
  }
}
</style>
