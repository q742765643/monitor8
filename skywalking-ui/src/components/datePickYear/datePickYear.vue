<template>
  <div>
    <a-date-picker
      mode="year"
      @panelChange="onchange"
      :open="showYearPicker"
      @focus="focus"
      :format="format"
      v-model="year"
      :size="size"
      :default-value="moment(moment().format(format) + '', format)"
    >
    </a-date-picker>
  </div>
</template>

<script>
/*  */
import moment from 'moment';
export default {
  props: ['size', 'format'],
  data() {
    return {
      showYearPicker: false,
      year: '',
    };
  },

  methods: {
    moment,
    onchange(date, datastring) {
      this.showYearPicker = false;
      this.year = date;
      this.$emit('change', date, moment(date).format(this.format));
    },
    focus() {
      this.showYearPicker = true;
    },
  },
};
</script>

<style lang="scss" scoped></style>
