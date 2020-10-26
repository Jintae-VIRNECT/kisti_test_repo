<template>
  <footer class="footer">
    <p><strong>©VIRNECT CO., LTD.</strong> All rights reserved.</p>
  </footer>
</template>

<script>
export default {}
</script>

<style lang="scss">
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 3.5714rem;
  background: #e0e4eb;
  & > p {
    color: #656f84;
    font-weight: 500;
    font-size: 0.8571rem;
    text-align: center;

    > strong {
      font-weight: bold;
    }
  }
}
</style>
