<template>
  <section class="dashboard">
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
    <card :category="'테스트 데이터'" :title="'테스트 타이틀'">
      <div style="height:200px">sdfdsfdsff</div>
    </card>
  </section>
</template>

<script>
import Card from 'components/modules/Card'
export default {
  name: 'Dashboard',
  components: {
    Card,
  },
}
</script>

<style lang="scss">
.dashboard {
  display: grid;
  grid-auto-flow: dense;
  grid-template-rows: repeat(3, 1fr);
  grid-template-columns: repeat(3, 1fr);
  gap: 30px 30px;
  width: 100%;
  // height: 100%;
}
.card:nth-child(1) {
  grid-column: span 3;
}
</style>
