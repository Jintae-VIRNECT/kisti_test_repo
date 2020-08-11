<template>
  <section class="search">
    <span>기간 검색</span>
    <!-- 캘린더 컴포넌트 -->
    <div class="search__wrapper">
      <input class="search--input" type="text" />
      <button class="search--button">검색</button>
    </div>
  </section>
</template>

<script>
export default {
  name: 'RemoteSearchBar',
}
</script>

<style lang="scss">
.search {
  display: flex;
  width: 1280px;
  height: 100px;
  background: #f5f7fa;
  border-radius: 0px;
}

.search__wrapper {
  display: flex;
  width: 436px;
  height: 48px;
}

.search--input {
  width: 332px;
  height: 100%;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(229, 231, 233);
  border-radius: 0px;
}

.search--button {
  width: 104px;
  height: 100%;
  background: rgb(20, 90, 182);
  border-radius: 0px;
}
</style>
