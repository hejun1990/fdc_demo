/**
 * Created by hejun-FDC on 2017/4/1.
 */
var vm = new Vue({
    el: '#addArticleForm',
    data: {
        articleType: "",
        title: "",
        author: "",
        labelDescription: "",
        keyWords: "",
        digest: "",
        content: ""
    },
    methods: {
        submitArticle: function () {
            $.getJSON(web_project + "/addarticle", this.$data, function(result){
                if(result.data) {
                    alert("添加成功");
                }else {
                    alert("添加失败");
                }
            });
        }
    }
})