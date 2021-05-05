package com.github.compose_playground.architecture.data

/**
 * apkLink :
 * author : keycoding
 * chapterId : 346
 * chapterName : JVM
 * collect : false
 * courseId : 13
 * desc :
 * envelopePic :
 * fresh : false
 * id : 2925
 * link : https://blog.csdn.net/yfqnihao/article/details/8289363
 * niceDate : 2018-05-15
 * origin :
 * projectLink :
 * publishTime : 1526374833000
 * superChapterId : 245
 * superChapterName : Java深入
 * tags : []
 * title : java之jvm学习笔记十三(jvm基本结构)
 * type : 0
 * userId : -1
 * visible : 1
 * zan : 0
 */

data class ArticleBean(
    val apkLink: String? = null,
    val author: String? = null,
    val chapterId: Int = 0,
    val chapterName: String? = null,
    val collect: Boolean = false,
    val courseId: Int = 0,
    val desc: String? = null,
    val envelopePic: String? = null,
    val isFresh: Boolean = false,
    val id: Int = 0,
    val link: String? = null,
    val niceDate: String? = null,
    val origin: String? = null,
    val projectLink: String? = null,
    val publishTime: Long = 0,
    val superChapterId: Int = 0,
    val superChapterName: String? = null,
    val title: String? = null,
    val type: Int = 0,
    val userId: Int = 0,
    val visible: Int = 0,
    val zan: Int = 0,
    val tags: List<*>? = null
)