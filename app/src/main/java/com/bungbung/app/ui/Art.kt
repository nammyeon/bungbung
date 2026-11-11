// 이 파일은 tools/art_gen.py 로 만든다. 직접 고치지 말고 생성기를 고칠 것.
package com.bungbung.app.ui

internal const val OUT = 1L
internal const val BODY = 2L

@Suppress("LargeClass")
object Art {
    val bungBack = Drawing(0f, -22f, 120f, 142f, listOf(
        L("M18,108 a42,6 0 1,0 84,0 a42,6 0 1,0 -84,0 Z", 0x22000000L, null, 0f),
        L("M22,96 a12,12 0 1,0 24,0 a12,12 0 1,0 -24,0 Z", 0xFF5B5566L, OUT, 2.6f),
        L("M74,96 a12,12 0 1,0 24,0 a12,12 0 1,0 -24,0 Z", 0xFF5B5566L, OUT, 2.6f),
        L("M29.5,96 a4.5,4.5 0 1,0 9,0 a4.5,4.5 0 1,0 -9,0 Z", 0xFFE9E3F0L, null, 0f),
        L("M81.5,96 a4.5,4.5 0 1,0 9,0 a4.5,4.5 0 1,0 -9,0 Z", 0xFFE9E3F0L, null, 0f),
        L("M32,48 C33,13 87,13 88,48 Z", BODY, OUT, 3f),
        L("M41,46 C42,23 78,23 79,46 Z", 0xFFD6EEFFL, OUT, 2.4f),
        L("M49,33 Q52,28 57,27", null, 0xFFFFFFFFL, 2.6f),
        L("M15,73 C13,50 30,41 60,41 C90,41 107,50 105,73 C107,93 92,103 60,103 C28,103 13,93 15,73 Z", BODY, OUT, 3.2f),
        L("M17,77 a5,6.5 0 1,0 10,0 a5,6.5 0 1,0 -10,0 Z", 0xFFFFF4B8L, OUT, 2.2f),
        L("M93,77 a5,6.5 0 1,0 10,0 a5,6.5 0 1,0 -10,0 Z", 0xFFFFF4B8L, OUT, 2.2f),
        L("M29.5,81 a7.5,4.2 0 1,0 15,0 a7.5,4.2 0 1,0 -15,0 Z", 0xFFFFB3C1L, null, 0f),
        L("M75.5,81 a7.5,4.2 0 1,0 15,0 a7.5,4.2 0 1,0 -15,0 Z", 0xFFFFB3C1L, null, 0f),
    ))
    val faceCalm = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M43.4,71 a3.6,4.4 0 1,0 7.2,0 a3.6,4.4 0 1,0 -7.2,0 Z", OUT, null, 0f),
        L("M69.4,71 a3.6,4.4 0 1,0 7.2,0 a3.6,4.4 0 1,0 -7.2,0 Z", OUT, null, 0f),
        L("M47,69.4 a1.2,1.2 0 1,0 2.4,0 a1.2,1.2 0 1,0 -2.4,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M73,69.4 a1.2,1.2 0 1,0 2.4,0 a1.2,1.2 0 1,0 -2.4,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M56,80 Q60,84 64,80", null, OUT, 2.4f),
    ))
    val faceHappy = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M42,73 Q47,65 52,73", null, OUT, 2.9f),
        L("M68,73 Q73,65 78,73", null, OUT, 2.9f),
        L("M53.5,78.5 Q60,90 66.5,78.5 Z", 0xFFFF8E9EL, OUT, 2.4f),
    ))
    val faceScared = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M41.4,70 a5.6,6.6 0 1,0 11.2,0 a5.6,6.6 0 1,0 -11.2,0 Z", 0xFFFFFFFFL, OUT, 2.2f),
        L("M67.4,70 a5.6,6.6 0 1,0 11.2,0 a5.6,6.6 0 1,0 -11.2,0 Z", 0xFFFFFFFFL, OUT, 2.2f),
        L("M44.6,71 a2.4,2.9 0 1,0 4.8,0 a2.4,2.9 0 1,0 -4.8,0 Z", OUT, null, 0f),
        L("M70.6,71 a2.4,2.9 0 1,0 4.8,0 a2.4,2.9 0 1,0 -4.8,0 Z", OUT, null, 0f),
        L("M53,83 q2.3,-3 4.6,0 q2.3,3 4.6,0 q2.3,-3 4.6,0", null, OUT, 2.2f),
        L("M101,46 q6,9 0,12.5 q-6,-3.5 0,-12.5 Z", 0xFFAEE2FFL, OUT, 2f),
    ))
    val faceSulky = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M41,70 L52,72", null, OUT, 3f),
        L("M68,72 L79,70", null, OUT, 3f),
        L("M55,83 Q60,79.5 65,83", null, OUT, 2.4f),
        L("M93,47 l7,0 M96.5,43.5 l0,7", null, 0xFFFF6B6BL, 2.6f),
        L("M28,81 a9,5.2 0 1,0 18,0 a9,5.2 0 1,0 -18,0 Z", 0xFFFF9DB0L, null, 0f),
        L("M74,81 a9,5.2 0 1,0 18,0 a9,5.2 0 1,0 -18,0 Z", 0xFFFF9DB0L, null, 0f),
    ))
    val faceHungry = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M42,69 Q47,74 52,69", null, OUT, 2.8f),
        L("M68,69 Q73,74 78,69", null, OUT, 2.8f),
        L("M55.8,82 a4.2,4.8 0 1,0 8.4,0 a4.2,4.8 0 1,0 -8.4,0 Z", 0xFFFF8E9EL, OUT, 2.2f),
        L("M64.5,85 q1,6 3.2,6.2 q2.2,0 1,-4.5", 0xFFBFE8FFL, OUT, 1.6f),
    ))
    val faceSick = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M43,67 L51,75 M51,67 L43,75", null, OUT, 2.6f),
        L("M69,67 L77,75 M77,67 L69,75", null, OUT, 2.6f),
        L("M54,82 q3,-3 6,0 q3,3 6,0", null, OUT, 2.2f),
        L("M68,52 l15,-5 l2.2,6.4 l-15,5 Z", 0xFFF6D3A8L, OUT, 1.8f),
        L("M74,52 l3,-1 M75,55 l3,-1", null, 0xFFD9A878L, 1.4f),
    ))
    val faceSleepy = Drawing(0f, 0f, 0f, 0f, listOf(
        L("M42,71 Q47,75 52,71", null, OUT, 2.8f),
        L("M68,71 Q73,75 78,71", null, OUT, 2.8f),
        L("M57.4,82 a2.6,2.4 0 1,0 5.2,0 a2.6,2.4 0 1,0 -5.2,0 Z", 0xFF7A5A5AL, null, 0f),
        L("M96,30 l8,0 l-8,8 l8,0", null, OUT, 2.4f),
        L("M108,17 l6,0 l-6,6 l6,0", null, OUT, 2.2f),
    ))
    val item_seoul = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M20,3 L20,14", null, OUT, 2.4f),
        L("M12,14 L28,14 L25.5,21 L14.5,21 Z", 0xFFFF9FB2L, OUT, 2.6f),
        L("M17.5,21 L15.5,37 L24.5,37 L22.5,21", 0xFFF3F0FAL, OUT, 2.6f),
    ))
    val item_hanam = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M14,17 q0,-10 6,-10 q6,0 6,10", null, OUT, 2.4f),
        L("M8.5,17 L31.5,17 L33.5,37 L6.5,37 Z", 0xFFFFC2D1L, OUT, 2.6f),
        L("M17,26 a3,3 0 1,0 6,0 a3,3 0 1,0 -6,0 Z", 0xFFFFFFFFL, null, 0f),
    ))
    val item_yangpyeong = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M4,27 a16,9.5 0 1,0 32,0 a16,9.5 0 1,0 -32,0 Z", 0xFF8ED081L, OUT, 2.6f),
        L("M20,27 L20,18 M20,27 L8,23 M20,27 L32,23", null, 0xFF5E9C55L, 1.8f),
        L("M20,3 q7,7 0,14 q-7,-7 0,-14 Z", 0xFFFFB3C7L, OUT, 2.2f),
    ))
    val item_hoengseong = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M5,24 q2,-12 16,-12 q15,0 14,12 q-1,12 -15,12 q-16,0 -15,-12 Z", 0xFFD47A68L, OUT, 2.6f),
        L("M9,23 q6,-5 12,0 q6,5 12,0", null, 0xFFF3C9B8L, 2f),
        L("M25,18 a3,2.2 0 1,0 6,0 a3,2.2 0 1,0 -6,0 Z", 0xFFF7E3D3L, null, 0f),
    ))
    val item_pyeongchang = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M1,24 L8,24 M32,24 L39,24", null, 0xFFFF8FA3L, 3.4f),
        L("M8,17 Q20,12.5 32,17 Q35.5,24 32,31 Q20,27 8,31 Q4.5,24 8,17 Z", 0xFF9AD7FFL, OUT, 2.6f),
        L("M20,16 L20,29.5", null, OUT, 2f),
        L("M12,20 q3,-2 5,0", null, 0xFFFFFFFFL, 2f),
    ))
    val item_gangneung = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M14,12 q-3,-4 0,-8 M22,12 q-3,-4 0,-8", null, 0xFFBCAAA4L, 2f),
        L("M29,21 q8,0 7,7 q-1,6 -8,4.5", null, OUT, 2.6f),
        L("M9,18 L31,18 L29,34 Q20,38 11,34 Z", 0xFFFFFFFFL, OUT, 2.6f),
        L("M9,19 a11,2.8 0 1,0 22,0 a11,2.8 0 1,0 -22,0 Z", 0xFF8B5A3CL, OUT, 2f),
        L("M17,27 a3,2 0 1,0 6,0 a3,2 0 1,0 -6,0 Z", 0xFFFFB3C1L, null, 0f),
    ))
    val item_sokcho = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M12.5,24 q-1,7 -3.5,11.5 M17.5,24 q0,7 -1,12 M22.5,24 q0,7 1,12 M27.5,24 q1,7 3.5,11.5", null, OUT, 2.4f),
        L("M20,3 Q31,10 29,25 L11,25 Q9,10 20,3 Z", 0xFFFFD9E2L, OUT, 2.6f),
        L("M14.3,18 a1.7,1.9 0 1,0 3.4,0 a1.7,1.9 0 1,0 -3.4,0 Z", OUT, null, 0f),
        L("M22.3,18 a1.7,1.9 0 1,0 3.4,0 a1.7,1.9 0 1,0 -3.4,0 Z", OUT, null, 0f),
    ))
    val item_chuncheon = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M15,23 L8,33", null, OUT, 4.4f),
        L("M15,23 L8,33", null, 0xFFFFFFFFL, 1.6f),
        L("M3.8,34 a3.2,3.2 0 1,0 6.4,0 a3.2,3.2 0 1,0 -6.4,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M17,7 q13,-2 15,9 q2,11 -11,14 q-9,1 -9,-10 q0,-12 5,-13 Z", 0xFFE79A52L, OUT, 2.6f),
    ))
    val item_daejeon = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M5,31 Q3,14 20,13 Q37,14 35,31 Z", 0xFFE3A25CL, OUT, 2.6f),
        L("M10,21 q3,-3 6,0 M18.5,18.5 q3,-3 6,0 M26,21.5 q2.4,-2.4 4.8,0", null, 0xFFB8723AL, 2f),
    ))
    val item_jeonju = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M20,30 L20,38", null, OUT, 3.4f),
        L("M20,30 L5,12 Q20,3 35,12 Z", 0xFFFFD36BL, OUT, 2.6f),
        L("M20,30 L12.5,7.5 M20,30 L20,5 M20,30 L27.5,7.5", null, 0xFFC9962FL, 1.6f),
        L("M17.6,30 a2.4,2.4 0 1,0 4.8,0 a2.4,2.4 0 1,0 -4.8,0 Z", 0xFFFF8FA3L, OUT, 1.6f),
    ))
    val item_gyeongju = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M14,7 h12 v5 h-12 Z", 0xFFE8D6B0L, OUT, 2.2f),
        L("M12.5,37 Q11.5,22 15,12 L25,12 Q28.5,22 27.5,37 Z", 0xFFE8D6B0L, OUT, 2.6f),
        L("M14,19 h12 M13,26 h14 M12.5,32 h15", null, 0xFFC9B48AL, 1.6f),
        L("M17.2,23 a2.8,2.8 0 1,0 5.6,0 a2.8,2.8 0 1,0 -5.6,0 Z", 0xFF6B5A4AL, null, 0f),
    ))
    val item_busan = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M12,25 a8,5.5 0 1,0 16,0 a8,5.5 0 1,0 -16,0 Z", 0xFFFFFFFFL, OUT, 2.6f),
        L("M4,21 Q12,12 19,22 M21,22 Q28,12 36,21", null, OUT, 2.8f),
        L("M27,24 l5,1.5 l-5,1.5 Z", 0xFFFFC24DL, OUT, 1.6f),
        L("M22.8,23.5 a1.2,1.2 0 1,0 2.4,0 a1.2,1.2 0 1,0 -2.4,0 Z", OUT, null, 0f),
    ))
    val item_yeosu = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M20,2 v6", null, OUT, 2f),
        L("M10,11 Q20,7 30,11 Q35,22 30,33 Q20,37 10,33 Q5,22 10,11 Z", 0xFFFF8B7BL, OUT, 2.6f),
        L("M14,9 h12 M14,35 h12", null, OUT, 3f),
        L("M12,22 h16", null, 0xFFFFD0C8L, 2f),
        L("M16.4,22 a3.6,5 0 1,0 7.2,0 a3.6,5 0 1,0 -7.2,0 Z", 0xFFFFE7A3L, null, 0f),
    ))
    val item_jeju = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M5.5,25 a14.5,11.5 0 1,0 29,0 a14.5,11.5 0 1,0 -29,0 Z", 0xFFFFA53DL, OUT, 2.6f),
        L("M20,14.5 v-4.5", null, OUT, 2.2f),
        L("M20.5,13 q2,-7.5 9.5,-7.5 q-1,7.5 -9.5,7.5 Z", 0xFF7CC66BL, OUT, 2f),
        L("M12,21 a3,2 0 1,0 6,0 a3,2 0 1,0 -6,0 Z", 0xFFFFD29AL, null, 0f),
    ))
    val itemUnknown = Drawing(0f, 0f, 40f, 40f, listOf(
        L("M6,22 a14,14 0 1,0 28,0 a14,14 0 1,0 -28,0 Z", 0xFFEDE6DDL, 0xFFCFC4B6L, 2.4f),
        L("M15.5,18 q0,-5 4.5,-5 q4.5,0 4.5,4.5 q0,3 -4.5,4.5 v3", null, 0xFFCFC4B6L, 2.8f),
        L("M18.4,30.5 a1.6,1.6 0 1,0 3.2,0 a1.6,1.6 0 1,0 -3.2,0 Z", 0xFFCFC4B6L, null, 0f),
    ))
    val iconSnack = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M24,7 Q39,25 39,34 Q39,43 24,43 Q9,43 9,34 Q9,25 24,7 Z", 0xFFFFFFFFL, OUT, 2.6f),
        L("M17,32 h14 v11 h-14 Z", 0xFF3E4A3DL, OUT, 2.2f),
        L("M17.2,25 a1.8,2 0 1,0 3.6,0 a1.8,2 0 1,0 -3.6,0 Z", OUT, null, 0f),
        L("M27.2,25 a1.8,2 0 1,0 3.6,0 a1.8,2 0 1,0 -3.6,0 Z", OUT, null, 0f),
        L("M12.4,28.5 a2.6,1.6 0 1,0 5.2,0 a2.6,1.6 0 1,0 -5.2,0 Z", 0xFFFFB3C1L, null, 0f),
        L("M30.4,28.5 a2.6,1.6 0 1,0 5.2,0 a2.6,1.6 0 1,0 -5.2,0 Z", 0xFFFFB3C1L, null, 0f),
    ))
    val iconPet = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M14,41 Q7,29 11,21 Q13.5,17.5 17,20.5 L19,26 L19,12 Q21,8 24,12 L24,24 L25,10 Q28,6.5 30.5,11 L30.5,25 L32,15 Q35,12 37,16 L37,30 Q37,43 26,43 Z", 0xFFFFD9C7L, OUT, 2.6f),
        L("M38,4.5 q3,-4 6,0 q2,3.4 -6,8.5 q-8,-5.1 -6,-8.5 q3,-4 6,0 Z", 0xFFFF8FA3L, OUT, 1.8f),
    ))
    val iconDex = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M5,12 Q14.5,7.5 24,13 L24,41 Q14.5,35.5 5,40 Z", 0xFF9BD3F7L, OUT, 2.6f),
        L("M24,13 Q33.5,7.5 43,12 L43,40 Q33.5,35.5 24,41 Z", 0xFFB6E0FAL, OUT, 2.6f),
        L("M30,20 l2,4 l4.4,0.6 l-3.2,3 l0.8,4.4 l-4,-2.2 l-4,2.2 l0.8,-4.4 l-3.2,-3 l4.4,-0.6 Z", 0xFFFFD45CL, OUT, 1.6f),
    ))
    val iconKey = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M21,21.5 h21 v5 h-4 v6 h-5 v-6 h-12 Z", 0xFFFFD45CL, OUT, 2.6f),
        L("M5,24 a9,9 0 1,0 18,0 a9,9 0 1,0 -18,0 Z", 0xFFFFD45CL, OUT, 2.6f),
        L("M10.6,24 a3.4,3.4 0 1,0 6.8,0 a3.4,3.4 0 1,0 -6.8,0 Z", 0xFFFFFFFFL, OUT, 2f),
    ))
    val iconFuel = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M31,15 q8,0 8,8 v10 q0,4.5 -3.5,4.5", null, OUT, 2.8f),
        L("M9,10 Q9,6 13,6 L28,6 Q32,6 32,10 L32,43 L9,43 Z", 0xFFFF9E9EL, OUT, 2.6f),
        L("M13.5,11 h14 v10 h-14 Z", 0xFFFFFFFFL, OUT, 2.2f),
        L("M6,43 h29", null, OUT, 3f),
    ))
    val iconGo = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M12,6 L12,44", null, OUT, 3f),
        L("M12,7 L38,7 L32,15 L38,23 L12,23 Z", 0xFF8FD9B6L, OUT, 2.6f),
        L("M12,7 h6.5 v8 h-6.5 Z M25,7 h6.5 v8 h-6.5 Z M18.5,15 h6.5 v8 h-6.5 Z", 0xFF3D3030L, null, 0f),
    ))
    val iconDemo = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M13,9 h5 q4,0 4,4 v5 q0,4 -4,4 h-5 q-4,0 -4,-4 v-5 q0,-4 4,-4 Z", 0xFF8FD9B6L, null, 0f),
        L("M30,9 h5 q4,0 4,4 v5 q0,4 -4,4 h-5 q-4,0 -4,-4 v-5 q0,-4 4,-4 Z", 0xFF8FD9B6L, null, 0f),
        L("M13,26 h5 q4,0 4,4 v5 q0,4 -4,4 h-5 q-4,0 -4,-4 v-5 q0,-4 4,-4 Z", 0xFF8FD9B6L, null, 0f),
        L("M30,26 h5 q4,0 4,4 v5 q0,4 -4,4 h-5 q-4,0 -4,-4 v-5 q0,-4 4,-4 Z", 0xFF8FD9B6L, null, 0f),
    ))
    val iconHeart = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M24,40 C8,30 4,20 9,13 C14,6 22,9 24,15 C26,9 34,6 39,13 C44,20 40,30 24,40 Z", 0xFFFF6B7DL, OUT, 2.8f),
        L("M13,16 q2,-4 6,-3", null, 0xFFFFFFFFL, 2.6f),
    ))
    val iconLove = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M6,24 a18,18 0 1,0 36,0 a18,18 0 1,0 -36,0 Z", 0xFFB794F6L, null, 0f),
        L("M15.6,21 a2.4,2.8 0 1,0 4.8,0 a2.4,2.8 0 1,0 -4.8,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M27.6,21 a2.4,2.8 0 1,0 4.8,0 a2.4,2.8 0 1,0 -4.8,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M16,28 Q24,37 32,28", null, 0xFFFFFFFFL, 3f),
    ))
    val iconFood = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M15,6 v12 q0,5 4,6 v18 M19,6 v11 M23,6 v12 q0,5 -4,6", null, 0xFF4CB97FL, 3.2f),
        L("M33,6 q-6,4 -6,14 q0,4 4,4 v18", null, 0xFF4CB97FL, 3.2f),
        L("M33,6 v36", null, 0xFF4CB97FL, 3.2f),
    ))
    val iconClose = Drawing(0f, 0f, 48f, 48f, listOf(
        L("M6,24 a18,18 0 1,0 36,0 a18,18 0 1,0 -36,0 Z", 0xFFFFFFFFL, OUT, 2.6f),
        L("M17,17 L31,31 M31,17 L17,31", null, OUT, 3.2f),
    ))
    val room = Drawing(0f, 0f, 400f, 240f, listOf(
        L("M0,0 H400 V150 H0 Z", 0xFFFFF6E6L, null, 0f),
        L("M14,0 v108 M42,0 v108 M70,0 v108 M98,0 v108 M126,0 v108 M154,0 v108 M182,0 v108 M210,0 v108 M238,0 v108 M266,0 v108 M294,0 v108 M322,0 v108 M350,0 v108 M378,0 v108", null, 0xFFFFEED6L, 9f),
        L("M0,108 H400 V150 H0 Z", 0xFFE4F3EAL, null, 0f),
        L("M0,108 H400", null, 0xFFC9E6D5L, 4f),
        L("M0,150 H400 V240 H0 Z", 0xFFEBD5B8L, null, 0f),
        L("M0,172 h400 M0,196 h400 M0,220 h400 M40,150 v22 M130,150 v22 M220,150 v22 M310,150 v22 M85,172 v24 M175,172 v24 M265,172 v24 M355,172 v24 M20,196 v24 M110,196 v24 M200,196 v24 M290,196 v24 M380,196 v24 M60,220 v20 M150,220 v20 M240,220 v20 M330,220 v20", null, 0xFFDCC2A0L, 1.6f),
        L("M0,150 H400", null, 0xFFCDB08CL, 3f),
        L("M0,8 Q17,20 34,10 T68,10 T102,10 T136,10 T170,10 T204,10 T238,10 T272,10 T306,10 T340,10 T374,10 T408,10", null, 0xFF8A7A70L, 1.4f),
        L("M16.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFF9FB2L, OUT, 1.6f),
        L("M50.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFFD45CL, OUT, 1.6f),
        L("M84.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFF8FD9B6L, OUT, 1.6f),
        L("M118.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFF9BD3F7L, OUT, 1.6f),
        L("M152.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFB794F6L, OUT, 1.6f),
        L("M186.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFF9FB2L, OUT, 1.6f),
        L("M220.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFFD45CL, OUT, 1.6f),
        L("M254.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFF8FD9B6L, OUT, 1.6f),
        L("M288.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFF9BD3F7L, OUT, 1.6f),
        L("M322.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFB794F6L, OUT, 1.6f),
        L("M356.8,16 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFF9FB2L, OUT, 1.6f),
        L("M390.8,21 a3.2,4.4 0 1,0 6.4,0 a3.2,4.4 0 1,0 -6.4,0 Z", 0xFFFFD45CL, OUT, 1.6f),
        L("M 67.4,30.8 h 82.8 q 5.4,0,5.4,5.4 v 59.4 q 0,5.4,-5.4,5.4 h -82.8 q -5.4,0,-5.4,-5.4 v -59.4 q 0,-5.4,5.4,-5.4 Z", 0xFFBFE6FFL, OUT, 3f),
        L("M 74.6,48.8 a 9,4.5,0,1,0,18,0 a 9,4.5,0,1,0,-18,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M 87.2,47 a 7.2,5.4,0,1,0,14.4,0 a 7.2,5.4,0,1,0,-14.4,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M 124.1,56 a 8.1,4.05,0,1,0,16.2,0 a 8.1,4.05,0,1,0,-16.2,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M 64.7,86.6 q 7.2,-12.6,14.4,-5.4 q 5.4,-10.8,13.5,-2.7 q 7.2,-9,13.5,0 q 8.1,-8.1,14.4,0.9 q 7.2,-7.2,13.5,1.8 q 7.2,-5.4,13.5,2.7 L 152.9,98.3 L 64.7,98.3 Z", 0xFF8ED081L, OUT, 2f),
        L("M 108.8,30.8 V 101 M 62,65.9 H 155.6", null, 0xFFB08A62L, 5f),
        L("M 67.4,30.8 h 82.8 q 5.4,0,5.4,5.4 v 59.4 q 0,5.4,-5.4,5.4 h -82.8 q -5.4,0,-5.4,-5.4 v -59.4 q 0,-5.4,5.4,-5.4 Z", null, OUT, 3f),
        L("M 54.8,27.2 H 162.8", null, OUT, 3.4f),
        L("M 51.74,27.2 a 3.06,3.06,0,1,0,6.12,0 a 3.06,3.06,0,1,0,-6.12,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M 159.74,27.2 a 3.06,3.06,0,1,0,6.12,0 a 3.06,3.06,0,1,0,-6.12,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M 58.4,27.2 Q 63.8,65,54.8,102.8 L 71,102.8 Q 76.4,65,72.8,27.2 Z", 0xFFFFFFFFL, OUT, 2.4f),
        L("M 159.2,27.2 Q 153.8,65,162.8,102.8 L 146.6,102.8 Q 141.2,65,144.8,27.2 Z", 0xFFFFFFFFL, OUT, 2.4f),
        L("M 62,73.1 Q 65.6,70.4,69.2,74 M 148.4,74 Q 152,70.4,155.6,73.1", null, 0xFFFF9FB2L, 3f),
        L("M 222.7,34.2 h 25.2 q 8.1,0,8.1,8.1 v 25.2 q 0,8.1,-8.1,8.1 h -25.2 q -8.1,0,-8.1,-8.1 v -25.2 q 0,-8.1,8.1,-8.1 Z", 0xFF7CB7F2L, OUT, 3f),
        L("M 229.9,64.8 V 45 h 7.2 q 7.2,0,7.2,7.2 q 0,7.2,-7.2,7.2 h -7.2", null, 0xFFFFFFFFL, 5f),
        L("M 291,79 H 372", null, OUT, 4f),
        L("M 300,79 v 9 M 363,79 v 9", null, OUT, 3f),
        L("M 301.8,59.2 h 19.8 q 3.6,0,3.6,3.6 v 12.6 q 0,3.6,-3.6,3.6 h -19.8 q -3.6,0,-3.6,-3.6 v -12.6 q 0,-3.6,3.6,-3.6 Z", 0xFFFF9E9EL, OUT, 2.4f),
        L("M 305.4,59.2 q 0,-6.3,6.3,-6.3 q 6.3,0,6.3,6.3", null, OUT, 2.4f),
        L("M 333.3,65.5 a 11.7,11.7,0,1,0,23.4,0 a 11.7,11.7,0,1,0,-23.4,0 Z", 0xFF5B5566L, OUT, 2.6f),
        L("M 340.05,65.5 a 4.95,4.95,0,1,0,9.9,0 a 4.95,4.95,0,1,0,-9.9,0 Z", 0xFFE9E3F0L, OUT, 2f),
        L("M 361.2,79 v -14.4 l 5.4,-5.4 h 3.6 v 19.8 Z", 0xFFFFD45CL, OUT, 2.2f),
        L("M 28,162 q -10,-26,6,-40 q 4,18,-2,40 Z", 0xFF8ED081L, OUT, 2.2f),
        L("M 34,162 q 4,-30,22,-36 q -4,22,-16,36 Z", 0xFF7CC66BL, OUT, 2.2f),
        L("M 34,162 q -2,-24,-18,-30 q 2,18,12,30 Z", 0xFF9EDB8FL, OUT, 2.2f),
        L("M 18,162 h 34 l -4,26 h -26 Z", 0xFFF2A477L, OUT, 2.6f),
        L("M 16,162 h 38 v 6 h -38 Z", 0xFFF7B98FL, OUT, 2.4f),
        L("M 345,132 h 42 q 5,0,5,5 v 34 q 0,5,-5,5 h -42 q -5,0,-5,-5 v -34 q 0,-5,5,-5 Z", 0xFFC99A6BL, OUT, 2.6f),
        L("M 340,154 H 392", null, OUT, 2.2f),
        L("M 363,143 a 3,2,0,1,0,6,0 a 3,2,0,1,0,-6,0 Z", 0xFFFFE7A3L, OUT, 1.6f),
        L("M 363,165 a 3,2,0,1,0,6,0 a 3,2,0,1,0,-6,0 Z", 0xFFFFE7A3L, OUT, 1.6f),
        L("M 366,132 V 112", null, OUT, 2.4f),
        L("M 354,112 L 378,112 L 372,98 L 360,98 Z", 0xFFFFE7A3L, OUT, 2.4f),
        L("M92,188 a108,26 0 1,0 216,0 a108,26 0 1,0 -216,0 Z", 0xFFFFE1EAL, OUT, 2.6f),
        L("M105,188 a95,20 0 1,0 190,0 a95,20 0 1,0 -190,0 Z", null, 0xFFFFC2D1L, 1.8f),
    ))
    val road = Drawing(0f, 0f, 400f, 240f, listOf(
        L("M0,0 H400 V240 H0 Z", 0xFFCDEBFFL, null, 0f),
        L("M322,40 a18,18 0 1,0 36,0 a18,18 0 1,0 -36,0 Z", 0xFFFFE07AL, 0xFFFFC94DL, 3f),
        L("M0,150 Q60,96 130,132 Q190,92 260,128 Q330,96 400,124 V180 H0 Z", 0xFFC2E8B4L, null, 0f),
        L("M0,165 Q80,128 170,154 Q250,130 330,152 Q370,140 400,146 V185 H0 Z", 0xFFA5DB94L, OUT, 2.2f),
        L("M0,170 H400 V214 H0 Z", 0xFF9B97A8L, null, 0f),
        L("M0,170 H400 M0,214 H400", null, OUT, 2.6f),
        L("M0,214 H400 V240 H0 Z", 0xFF9ED68FL, null, 0f),
    ))
    val roadDash = Drawing(0f, 0f, 800f, 240f, listOf(
        L("M0,192 h22 M50,192 h22 M100,192 h22 M150,192 h22 M200,192 h22 M250,192 h22 M300,192 h22 M350,192 h22 M400,192 h22 M450,192 h22 M500,192 h22 M550,192 h22 M600,192 h22 M650,192 h22 M700,192 h22 M750,192 h22", null, 0xFFFFFFFFL, 4f),
    ))
    val roadTrees = Drawing(0f, 0f, 800f, 240f, listOf(
        L("M30,166 v-16", null, OUT, 3f),
        L("M15,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M22,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M160,166 v-16", null, OUT, 3f),
        L("M145,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M152,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M290,166 v-16", null, OUT, 3f),
        L("M275,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M282,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M420,166 v-16", null, OUT, 3f),
        L("M405,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M412,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M550,166 v-16", null, OUT, 3f),
        L("M535,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M542,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M680,166 v-16", null, OUT, 3f),
        L("M665,140 a15,15 0 1,0 30,0 a15,15 0 1,0 -30,0 Z", 0xFF7CC66BL, OUT, 2.4f),
        L("M672,136 a4,3 0 1,0 8,0 a4,3 0 1,0 -8,0 Z", 0xFFA8E09AL, null, 0f),
        L("M86,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M103,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
        L("M216,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M233,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
        L("M346,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M363,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
        L("M476,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M493,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
        L("M606,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M623,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
        L("M736,222 a9,5 0 1,0 18,0 a9,5 0 1,0 -18,0 Z", 0xFFFFB3C7L, OUT, 1.8f),
        L("M753,226 a6,4 0 1,0 12,0 a6,4 0 1,0 -12,0 Z", 0xFFFFE07AL, OUT, 1.8f),
    ))
    val roadClouds = Drawing(0f, 0f, 800f, 240f, listOf(
        L("M20,48 a20,9 0 1,0 40,0 a20,9 0 1,0 -40,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M42,42 a12,9 0 1,0 24,0 a12,9 0 1,0 -24,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M20,48 a20,9 0 0,0 40,0", null, OUT, 0.01f),
        L("M190,48 a20,9 0 1,0 40,0 a20,9 0 1,0 -40,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M212,42 a12,9 0 1,0 24,0 a12,9 0 1,0 -24,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M190,48 a20,9 0 0,0 40,0", null, OUT, 0.01f),
        L("M360,48 a20,9 0 1,0 40,0 a20,9 0 1,0 -40,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M382,42 a12,9 0 1,0 24,0 a12,9 0 1,0 -24,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M360,48 a20,9 0 0,0 40,0", null, OUT, 0.01f),
        L("M530,48 a20,9 0 1,0 40,0 a20,9 0 1,0 -40,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M552,42 a12,9 0 1,0 24,0 a12,9 0 1,0 -24,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M530,48 a20,9 0 0,0 40,0", null, OUT, 0.01f),
        L("M700,48 a20,9 0 1,0 40,0 a20,9 0 1,0 -40,0 Z", 0xFFFFFFFFL, OUT, 2f),
        L("M722,42 a12,9 0 1,0 24,0 a12,9 0 1,0 -24,0 Z", 0xFFFFFFFFL, null, 0f),
        L("M700,48 a20,9 0 0,0 40,0", null, OUT, 0.01f),
    ))
    val items: Map<String, Drawing> = mapOf("seoul" to item_seoul, "hanam" to item_hanam, "yangpyeong" to item_yangpyeong, "hoengseong" to item_hoengseong, "pyeongchang" to item_pyeongchang, "gangneung" to item_gangneung, "sokcho" to item_sokcho, "chuncheon" to item_chuncheon, "daejeon" to item_daejeon, "jeonju" to item_jeonju, "gyeongju" to item_gyeongju, "busan" to item_busan, "yeosu" to item_yeosu, "jeju" to item_jeju)
}
