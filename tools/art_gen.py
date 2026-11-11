# 붕붕 일러스트 데이터 생성기: Art.kt(안드로이드)와 preview SVG를 같은 데이터로 만든다.
import json, sys
OUT = 'OUT'; BODY = 'BODY'
INK_HEX = '#3D3030'
def P(d, fill=None, stroke=OUT, w=2.6): return dict(d=d, fill=fill, stroke=stroke, w=w)
def E(cx, cy, rx, ry, fill=None, stroke=OUT, w=2.6):
    d = f"M{cx-rx:g},{cy:g} a{rx:g},{ry:g} 0 1,0 {2*rx:g},0 a{rx:g},{ry:g} 0 1,0 {-2*rx:g},0 Z"
    return P(d, fill, stroke, w)
def R(x, y, w, h, r, fill=None, stroke=OUT, sw=2.6):
    d = (f"M{x+r:g},{y:g} h{w-2*r:g} q{r:g},0 {r:g},{r:g} v{h-2*r:g} q0,{r:g} {-r:g},{r:g} "
         f"h{-(w-2*r):g} q{-r:g},0 {-r:g},{-r:g} v{-(h-2*r):g} q0,{-r:g} {r:g},{-r:g} Z")
    return P(d, fill, stroke, sw)


import re as _re
def _xf_path(d, sx, sy, tx, ty):
    toks = _re.findall(r'[MmLlHhVvCcQqTtSsAaZz]|-?\d*\.?\d+(?:e-?\d+)?', d)
    out = []; cmd = None; i = 0; nums = []
    def flush():
        pass
    res = []
    j = 0
    while j < len(toks):
        t = toks[j]
        if t.isalpha():
            cmd = t; res.append(t); j += 1; continue
        # read args for one command group
        n = {'M':2,'L':2,'T':2,'H':1,'V':1,'C':6,'Q':4,'S':4,'A':7}[cmd.upper()]
        args = [float(x) for x in toks[j:j+n]]; j += n
        rel = cmd.islower(); C = cmd.upper()
        if C == 'A':
            rx, ry, rot, la, sw, x, y = args
            sw2 = sw if (sx * sy) > 0 else 1 - sw
            args = [abs(rx*sx), abs(ry*sy), rot, la, sw2, x*sx + (0 if rel else tx), y*sy + (0 if rel else ty)]
        elif C == 'H':
            args = [args[0]*sx + (0 if rel else tx)]
        elif C == 'V':
            args = [args[0]*sy + (0 if rel else ty)]
        else:
            args = [v*sx + (0 if rel else tx) if k % 2 == 0 else v*sy + (0 if rel else ty) for k, v in enumerate(args)]
        res.append(",".join(f"{a:.2f}".rstrip('0').rstrip('.') for a in args))
        if cmd in 'Mm': cmd = 'L' if cmd == 'M' else 'l'
    return " ".join(res)
def XF(layers, sx=1, sy=None, tx=0, ty=0):
    sy = sx if sy is None else sy
    return [dict(l, d=_xf_path(l['d'], sx, sy, tx, ty), w=l['w']) for l in layers]

D = {}   # name -> (x0, y0, w, h, layers)

# ── 붕붕이 ─────────────────────────────
D['bungBack'] = (0, -22, 120, 142, [
    E(60, 108, 42, 6, '#22000000', None, 0),
    E(34, 96, 12, 12, '#5B5566'), E(86, 96, 12, 12, '#5B5566'),
    E(34, 96, 4.5, 4.5, '#E9E3F0', None, 0), E(86, 96, 4.5, 4.5, '#E9E3F0', None, 0),
    P("M32,48 C33,13 87,13 88,48 Z", BODY, OUT, 3),
    P("M41,46 C42,23 78,23 79,46 Z", '#D6EEFF', OUT, 2.4),
    P("M49,33 Q52,28 57,27", None, '#FFFFFF', 2.6),
    P("M15,73 C13,50 30,41 60,41 C90,41 107,50 105,73 C107,93 92,103 60,103 C28,103 13,93 15,73 Z", BODY, OUT, 3.2),
    E(22, 77, 5, 6.5, '#FFF4B8', OUT, 2.2), E(98, 77, 5, 6.5, '#FFF4B8', OUT, 2.2),
    E(37, 81, 7.5, 4.2, '#FFB3C1', None, 0), E(83, 81, 7.5, 4.2, '#FFB3C1', None, 0),
])
D['faceCalm'] = (0, 0, 0, 0, [
    E(47, 71, 3.6, 4.4, OUT, None, 0), E(73, 71, 3.6, 4.4, OUT, None, 0),
    E(48.2, 69.4, 1.2, 1.2, '#FFFFFF', None, 0), E(74.2, 69.4, 1.2, 1.2, '#FFFFFF', None, 0),
    P("M56,80 Q60,84 64,80", None, OUT, 2.4)])
D['faceHappy'] = (0, 0, 0, 0, [
    P("M42,73 Q47,65 52,73", None, OUT, 2.9), P("M68,73 Q73,65 78,73", None, OUT, 2.9),
    P("M53.5,78.5 Q60,90 66.5,78.5 Z", '#FF8E9E', OUT, 2.4)])
D['faceScared'] = (0, 0, 0, 0, [
    E(47, 70, 5.6, 6.6, '#FFFFFF', OUT, 2.2), E(73, 70, 5.6, 6.6, '#FFFFFF', OUT, 2.2),
    E(47, 71, 2.4, 2.9, OUT, None, 0), E(73, 71, 2.4, 2.9, OUT, None, 0),
    P("M53,83 q2.3,-3 4.6,0 q2.3,3 4.6,0 q2.3,-3 4.6,0", None, OUT, 2.2),
    P("M101,46 q6,9 0,12.5 q-6,-3.5 0,-12.5 Z", '#AEE2FF', OUT, 2)])
D['faceSulky'] = (0, 0, 0, 0, [
    P("M41,70 L52,72", None, OUT, 3), P("M68,72 L79,70", None, OUT, 3),
    P("M55,83 Q60,79.5 65,83", None, OUT, 2.4),
    P("M93,47 l7,0 M96.5,43.5 l0,7", None, '#FF6B6B', 2.6),
    E(37, 81, 9, 5.2, '#FF9DB0', None, 0), E(83, 81, 9, 5.2, '#FF9DB0', None, 0)])
D['faceHungry'] = (0, 0, 0, 0, [
    P("M42,69 Q47,74 52,69", None, OUT, 2.8), P("M68,69 Q73,74 78,69", None, OUT, 2.8),
    E(60, 82, 4.2, 4.8, '#FF8E9E', OUT, 2.2),
    P("M64.5,85 q1,6 3.2,6.2 q2.2,0 1,-4.5", '#BFE8FF', OUT, 1.6)])
D['faceSick'] = (0, 0, 0, 0, [
    P("M43,67 L51,75 M51,67 L43,75", None, OUT, 2.6), P("M69,67 L77,75 M77,67 L69,75", None, OUT, 2.6),
    P("M54,82 q3,-3 6,0 q3,3 6,0", None, OUT, 2.2),
    P("M68,52 l15,-5 l2.2,6.4 l-15,5 Z", '#F6D3A8', OUT, 1.8),
    P("M74,52 l3,-1 M75,55 l3,-1", None, '#D9A878', 1.4)])
D['faceSleepy'] = (0, 0, 0, 0, [
    P("M42,71 Q47,75 52,71", None, OUT, 2.8), P("M68,71 Q73,75 78,71", None, OUT, 2.8),
    E(60, 82, 2.6, 2.4, '#7A5A5A', None, 0),
    P("M96,30 l8,0 l-8,8 l8,0", None, OUT, 2.4), P("M108,17 l6,0 l-6,6 l6,0", None, OUT, 2.2)])

# ── 도감 아이템 (40 x 40) ──────────────────────
I = {}
I['seoul'] = [P("M20,3 L20,14", None, OUT, 2.4), P("M12,14 L28,14 L25.5,21 L14.5,21 Z", '#FF9FB2'),
              P("M17.5,21 L15.5,37 L24.5,37 L22.5,21", '#F3F0FA')]
I['hanam'] = [P("M14,17 q0,-10 6,-10 q6,0 6,10", None, OUT, 2.4), P("M8.5,17 L31.5,17 L33.5,37 L6.5,37 Z", '#FFC2D1'),
              E(20, 26, 3, 3, '#FFFFFF', None, 0)]
I['yangpyeong'] = [E(20, 27, 16, 9.5, '#8ED081'), P("M20,27 L20,18 M20,27 L8,23 M20,27 L32,23", None, '#5E9C55', 1.8),
                   P("M20,3 q7,7 0,14 q-7,-7 0,-14 Z", '#FFB3C7', OUT, 2.2)]
I['hoengseong'] = [P("M5,24 q2,-12 16,-12 q15,0 14,12 q-1,12 -15,12 q-16,0 -15,-12 Z", '#D47A68'),
                   P("M9,23 q6,-5 12,0 q6,5 12,0", None, '#F3C9B8', 2), E(28, 18, 3, 2.2, '#F7E3D3', None, 0)]
I['pyeongchang'] = [P("M1,24 L8,24 M32,24 L39,24", None, '#FF8FA3', 3.4),
                    P("M8,17 Q20,12.5 32,17 Q35.5,24 32,31 Q20,27 8,31 Q4.5,24 8,17 Z", '#9AD7FF'),
                    P("M20,16 L20,29.5", None, OUT, 2), P("M12,20 q3,-2 5,0", None, '#FFFFFF', 2)]
I['gangneung'] = [P("M14,12 q-3,-4 0,-8 M22,12 q-3,-4 0,-8", None, '#BCAAA4', 2),
                  P("M29,21 q8,0 7,7 q-1,6 -8,4.5", None, OUT, 2.6),
                  P("M9,18 L31,18 L29,34 Q20,38 11,34 Z", '#FFFFFF'), E(20, 19, 11, 2.8, '#8B5A3C', OUT, 2),
                  E(20, 27, 3, 2, '#FFB3C1', None, 0)]
I['sokcho'] = [P("M12.5,24 q-1,7 -3.5,11.5 M17.5,24 q0,7 -1,12 M22.5,24 q0,7 1,12 M27.5,24 q1,7 3.5,11.5", None, OUT, 2.4),
               P("M20,3 Q31,10 29,25 L11,25 Q9,10 20,3 Z", '#FFD9E2'),
               E(16, 18, 1.7, 1.9, OUT, None, 0), E(24, 18, 1.7, 1.9, OUT, None, 0)]
I['chuncheon'] = [P("M15,23 L8,33", None, OUT, 4.4), P("M15,23 L8,33", None, '#FFFFFF', 1.6),
                  E(7, 34, 3.2, 3.2, '#FFFFFF', OUT, 2),
                  P("M17,7 q13,-2 15,9 q2,11 -11,14 q-9,1 -9,-10 q0,-12 5,-13 Z", '#E79A52')]
I['daejeon'] = [P("M5,31 Q3,14 20,13 Q37,14 35,31 Z", '#E3A25C'),
                P("M10,21 q3,-3 6,0 M18.5,18.5 q3,-3 6,0 M26,21.5 q2.4,-2.4 4.8,0", None, '#B8723A', 2)]
I['jeonju'] = [P("M20,30 L20,38", None, OUT, 3.4), P("M20,30 L5,12 Q20,3 35,12 Z", '#FFD36B'),
               P("M20,30 L12.5,7.5 M20,30 L20,5 M20,30 L27.5,7.5", None, '#C9962F', 1.6), E(20, 30, 2.4, 2.4, '#FF8FA3', OUT, 1.6)]
I['gyeongju'] = [P("M14,7 h12 v5 h-12 Z", '#E8D6B0', OUT, 2.2),
                 P("M12.5,37 Q11.5,22 15,12 L25,12 Q28.5,22 27.5,37 Z", '#E8D6B0'),
                 P("M14,19 h12 M13,26 h14 M12.5,32 h15", None, '#C9B48A', 1.6), E(20, 23, 2.8, 2.8, '#6B5A4A', None, 0)]
I['busan'] = [E(20, 25, 8, 5.5, '#FFFFFF'), P("M4,21 Q12,12 19,22 M21,22 Q28,12 36,21", None, OUT, 2.8),
              P("M27,24 l5,1.5 l-5,1.5 Z", '#FFC24D', OUT, 1.6), E(24, 23.5, 1.2, 1.2, OUT, None, 0)]
I['yeosu'] = [P("M20,2 v6", None, OUT, 2), P("M10,11 Q20,7 30,11 Q35,22 30,33 Q20,37 10,33 Q5,22 10,11 Z", '#FF8B7B'),
              P("M14,9 h12 M14,35 h12", None, OUT, 3), P("M12,22 h16", None, '#FFD0C8', 2), E(20, 22, 3.6, 5, '#FFE7A3', None, 0)]
I['jeju'] = [E(20, 25, 14.5, 11.5, '#FFA53D'), P("M20,14.5 v-4.5", None, OUT, 2.2),
             P("M20.5,13 q2,-7.5 9.5,-7.5 q-1,7.5 -9.5,7.5 Z", '#7CC66B', OUT, 2), E(15, 21, 3, 2, '#FFD29A', None, 0)]
for k, v in I.items(): D['item_' + k] = (0, 0, 40, 40, v)
D['itemUnknown'] = (0, 0, 40, 40, [E(20, 22, 14, 14, '#EDE6DD', '#CFC4B6', 2.4),
                                   P("M15.5,18 q0,-5 4.5,-5 q4.5,0 4.5,4.5 q0,3 -4.5,4.5 v3", None, '#CFC4B6', 2.8),
                                   E(20, 30.5, 1.6, 1.6, '#CFC4B6', None, 0)])

# ── 아이콘 (48 x 48) ──────────────────────────
D['iconSnack'] = (0, 0, 48, 48, [P("M24,7 Q39,25 39,34 Q39,43 24,43 Q9,43 9,34 Q9,25 24,7 Z", '#FFFFFF'),
                                 P("M17,32 h14 v11 h-14 Z", '#3E4A3D', OUT, 2.2),
                                 E(19, 25, 1.8, 2, OUT, None, 0), E(29, 25, 1.8, 2, OUT, None, 0), E(15, 28.5, 2.6, 1.6, '#FFB3C1', None, 0), E(33, 28.5, 2.6, 1.6, '#FFB3C1', None, 0)])
D['iconPet'] = (0, 0, 48, 48, [P("M14,41 Q7,29 11,21 Q13.5,17.5 17,20.5 L19,26 L19,12 Q21,8 24,12 L24,24 L25,10 Q28,6.5 30.5,11 L30.5,25 L32,15 Q35,12 37,16 L37,30 Q37,43 26,43 Z", '#FFD9C7'),
                               P("M38,4.5 q3,-4 6,0 q2,3.4 -6,8.5 q-8,-5.1 -6,-8.5 q3,-4 6,0 Z", '#FF8FA3', OUT, 1.8)])
D['iconDex'] = (0, 0, 48, 48, [P("M5,12 Q14.5,7.5 24,13 L24,41 Q14.5,35.5 5,40 Z", '#9BD3F7'),
                               P("M24,13 Q33.5,7.5 43,12 L43,40 Q33.5,35.5 24,41 Z", '#B6E0FA'),
                               P("M30,20 l2,4 l4.4,0.6 l-3.2,3 l0.8,4.4 l-4,-2.2 l-4,2.2 l0.8,-4.4 l-3.2,-3 l4.4,-0.6 Z", '#FFD45C', OUT, 1.6)])
D['iconKey'] = (0, 0, 48, 48, [P("M21,21.5 h21 v5 h-4 v6 h-5 v-6 h-12 Z", '#FFD45C'),
                               E(14, 24, 9, 9, '#FFD45C'), E(14, 24, 3.4, 3.4, '#FFFFFF', OUT, 2)])
D['iconFuel'] = (0, 0, 48, 48, [P("M31,15 q8,0 8,8 v10 q0,4.5 -3.5,4.5", None, OUT, 2.8),
                                P("M9,10 Q9,6 13,6 L28,6 Q32,6 32,10 L32,43 L9,43 Z", '#FF9E9E'),
                                P("M13.5,11 h14 v10 h-14 Z", '#FFFFFF', OUT, 2.2), P("M6,43 h29", None, OUT, 3)])
D['iconGo'] = (0, 0, 48, 48, [P("M12,6 L12,44", None, OUT, 3), P("M12,7 L38,7 L32,15 L38,23 L12,23 Z", '#8FD9B6'),
                              P("M12,7 h6.5 v8 h-6.5 Z M25,7 h6.5 v8 h-6.5 Z M18.5,15 h6.5 v8 h-6.5 Z", '#3D3030', None, 0)])
D['iconDemo'] = (0, 0, 48, 48, [R(9, 9, 13, 13, 4, '#8FD9B6', None, 0), R(26, 9, 13, 13, 4, '#8FD9B6', None, 0),
                                R(9, 26, 13, 13, 4, '#8FD9B6', None, 0), R(26, 26, 13, 13, 4, '#8FD9B6', None, 0)])
D['iconHeart'] = (0, 0, 48, 48, [P("M24,40 C8,30 4,20 9,13 C14,6 22,9 24,15 C26,9 34,6 39,13 C44,20 40,30 24,40 Z", '#FF6B7D', OUT, 2.8),
                                 P("M13,16 q2,-4 6,-3", None, '#FFFFFF', 2.6)])
D['iconLove'] = (0, 0, 48, 48, [E(24, 24, 18, 18, '#B794F6', None, 0), E(18, 21, 2.4, 2.8, '#FFFFFF', None, 0), E(30, 21, 2.4, 2.8, '#FFFFFF', None, 0),
                                P("M16,28 Q24,37 32,28", None, '#FFFFFF', 3)])
D['iconFood'] = (0, 0, 48, 48, [P("M15,6 v12 q0,5 4,6 v18 M19,6 v11 M23,6 v12 q0,5 -4,6", None, '#4CB97F', 3.2),
                                P("M33,6 q-6,4 -6,14 q0,4 4,4 v18", None, '#4CB97F', 3.2), P("M33,6 v36", None, '#4CB97F', 3.2)])
D['iconClose'] = (0, 0, 48, 48, [E(24, 24, 18, 18, '#FFFFFF'), P("M17,17 L31,31 M31,17 L17,31", None, OUT, 3.2)])

# ── 배경: 아늑한 차고 방 (400 x 240) ─────────────
FLOOR = 150
def stripes():
    return " ".join(f"M{x},0 v108" for x in range(14, 400, 28))
def planks():
    s = f"M0,172 h400 M0,196 h400 M0,220 h400 "
    s += " ".join(f"M{x},{FLOOR} v22" for x in range(40, 400, 90)) + " "
    s += " ".join(f"M{x},172 v24" for x in range(85, 400, 90)) + " "
    s += " ".join(f"M{x},196 v24" for x in range(20, 400, 90)) + " "
    s += " ".join(f"M{x},220 v20" for x in range(60, 400, 90))
    return s
bulbs = []
cols = ['#FF9FB2', '#FFD45C', '#8FD9B6', '#9BD3F7', '#B794F6']
for i, x in enumerate(range(20, 400, 34)):
    y = 10 + (5 if i % 2 else 0)
    bulbs.append(E(x, y + 6, 3.2, 4.4, cols[i % 5], OUT, 1.6))
window = [
    R(60, 32, 104, 78, 6, '#BFE6FF', OUT, 3),
    E(84, 52, 10, 5, '#FFFFFF', None, 0), E(96, 50, 8, 6, '#FFFFFF', None, 0), E(138, 60, 9, 4.5, '#FFFFFF', None, 0),
    P("M63,94 q8,-14 16,-6 q6,-12 15,-3 q8,-10 15,0 q9,-9 16,1 q8,-8 15,2 q8,-6 15,3 L161,107 L63,107 Z", '#8ED081', OUT, 2),
    P("M112,32 V110 M60,71 H164", None, '#B08A62', 5),
    R(60, 32, 104, 78, 6, None, OUT, 3),
    P("M52,28 H172", None, OUT, 3.4), E(52, 28, 3.4, 3.4, '#FFFFFF', OUT, 2), E(172, 28, 3.4, 3.4, '#FFFFFF', OUT, 2),
    P("M56,28 Q62,70 52,112 L70,112 Q76,70 72,28 Z", '#FFFFFF', OUT, 2.4),
    P("M168,28 Q162,70 172,112 L154,112 Q148,70 152,28 Z", '#FFFFFF', OUT, 2.4),
    P("M60,79 Q64,76 68,80 M156,80 Q160,76 164,79", None, '#FF9FB2', 3),
]
poster = [R(214, 38, 46, 46, 9, '#7CB7F2', OUT, 3), P("M231,72 V50 h8 q8,0 8,8 q0,8 -8,8 h-8", None, '#FFFFFF', 5)]
shelf = [
    P("M290,90 H380", None, OUT, 4), P("M300,90 v10 M370,90 v10", None, OUT, 3),
    R(298, 68, 30, 22, 4, '#FF9E9E', OUT, 2.4), P("M306,68 q0,-7 7,-7 q7,0 7,7", None, OUT, 2.4),
    E(350, 75, 13, 13, '#5B5566', OUT, 2.6), E(350, 75, 5.5, 5.5, '#E9E3F0', OUT, 2),
    P("M368,90 v-16 l6,-6 h4 v22 Z", '#FFD45C', OUT, 2.2),
]
plant = [
    P("M24,150 q-10,-26 6,-40 q4,18 -2,40 Z", '#8ED081', OUT, 2.2),
    P("M30,150 q4,-30 22,-36 q-4,22 -16,36 Z", '#7CC66B', OUT, 2.2),
    P("M30,150 q-2,-24 -18,-30 q2,18 12,30 Z", '#9EDB8F', OUT, 2.2),
    P("M14,150 h34 l-4,26 h-26 Z", '#F2A477', OUT, 2.6), P("M12,150 h38 v6 h-38 Z", '#F7B98F', OUT, 2.4),
]
drawer = [
    R(340, 132, 52, 44, 5, '#C99A6B', OUT, 2.6), P("M340,154 H392", None, OUT, 2.2),
    E(366, 143, 3, 2, '#FFE7A3', OUT, 1.6), E(366, 165, 3, 2, '#FFE7A3', OUT, 1.6),
    P("M366,132 V112", None, OUT, 2.4), P("M354,112 L378,112 L372,98 L360,98 Z", '#FFE7A3', OUT, 2.4),
]
D['room'] = (0, 0, 400, 240, [
    P("M0,0 H400 V150 H0 Z", '#FFF6E6', None, 0),
    P(stripes(), None, '#FFEED6', 9),
    P("M0,108 H400 V150 H0 Z", '#E4F3EA', None, 0),
    P("M0,108 H400", None, '#C9E6D5', 4),
    P("M0,150 H400 V240 H0 Z", '#EBD5B8', None, 0),
    P(planks(), None, '#DCC2A0', 1.6),
    P("M0,150 H400", None, '#CDB08C', 3),
    P("M0,8 Q17,20 34,10 T68,10 T102,10 T136,10 T170,10 T204,10 T238,10 T272,10 T306,10 T340,10 T374,10 T408,10", None, '#8A7A70', 1.4),
    *bulbs,
    *XF(window, 0.9, tx=8, ty=2),
    *XF(poster, 0.9, tx=22, ty=0),
    *XF(shelf, 0.9, tx=30, ty=-2),
    *XF(plant, 1.0, tx=4, ty=12),
    *XF(drawer, 1.0, tx=0, ty=0),
    E(200, 188, 108, 26, '#FFE1EA', OUT, 2.6),
    E(200, 188, 95, 20, None, '#FFC2D1', 1.8),
])

# ── 배경: 도로 (400 x 240) ─────────────────────
D['road'] = (0, 0, 400, 240, [
    P("M0,0 H400 V240 H0 Z", '#CDEBFF', None, 0),
    E(340, 40, 18, 18, '#FFE07A', '#FFC94D', 3),
    P("M0,150 Q60,96 130,132 Q190,92 260,128 Q330,96 400,124 V180 H0 Z", '#C2E8B4', None, 0),
    P("M0,165 Q80,128 170,154 Q250,130 330,152 Q370,140 400,146 V185 H0 Z", '#A5DB94', OUT, 2.2),
    P("M0,170 H400 V214 H0 Z", '#9B97A8', None, 0),
    P("M0,170 H400 M0,214 H400", None, OUT, 2.6),
    P("M0,214 H400 V240 H0 Z", '#9ED68F', None, 0),
])
dash = " ".join(f"M{x},192 h22" for x in range(0, 800, 50))
D['roadDash'] = (0, 0, 800, 240, [P(dash, None, '#FFFFFF', 4)])
trees = []
for x in range(30, 800, 130):
    trees += [P(f"M{x},166 v-16", None, OUT, 3), E(x, 140, 15, 15, '#7CC66B', OUT, 2.4), E(x - 4, 136, 4, 3, '#A8E09A', None, 0)]
for x in range(95, 800, 130):
    trees += [E(x, 222, 9, 5, '#FFB3C7', OUT, 1.8), E(x + 14, 226, 6, 4, '#FFE07A', OUT, 1.8)]
D['roadTrees'] = (0, 0, 800, 240, trees)
clouds = []
for x in range(40, 800, 170):
    clouds += [E(x, 48, 20, 9, '#FFFFFF', OUT, 2), E(x + 14, 42, 12, 9, '#FFFFFF', None, 0), P(f"M{x-20},48 a20,9 0 0,0 40,0", None, OUT, 0.01)]
D['roadClouds'] = (0, 0, 800, 240, clouds)

# ─────────────────────────────────────────
def kt_color(c):
    if c is None: return 'null'
    if c == OUT: return 'OUT'
    if c == BODY: return 'BODY'
    h = c.lstrip('#')
    if len(h) == 6: h = 'FF' + h
    return f'0x{h.upper()}L'
def emit_kotlin(path):
    out = ['// 이 파일은 tools/art_gen.py 로 만든다. 직접 고치지 말고 생성기를 고칠 것.',
           'package com.bungbung.app.ui', '',
           'internal const val OUT = 1L', 'internal const val BODY = 2L', '',
           '@Suppress("LargeClass")', 'object Art {']
    for name, (x0, y0, w, h, layers) in D.items():
        kname = name
        out.append(f'    val {kname} = Drawing({x0}f, {y0}f, {w}f, {h}f, listOf(')
        for l in layers:
            out.append(f'        L("{l["d"]}", {kt_color(l["fill"])}, {kt_color(l["stroke"])}, {l["w"]}f),')
        out.append('    ))')
    out.append('    val items: Map<String, Drawing> = mapOf(' + ", ".join(f'"{k}" to item_{k}' for k in I) + ')')
    out.append('}')
    open(path, 'w').write("\n".join(out) + "\n")
def svg_layers(name, body='#FFE9A8', tx=0, ty=0, s=1):
    x0, y0, w, h, layers = D[name]
    g = [f'<g transform="translate({tx},{ty}) scale({s})">']
    for l in layers:
        fill = 'none' if l['fill'] is None else (INK_HEX if l['fill'] == OUT else body if l['fill'] == BODY else css(l['fill']))
        stroke = 'none' if l['stroke'] is None else (INK_HEX if l['stroke'] == OUT else css(l['stroke']))
        g.append(f'<path d="{l["d"]}" fill="{fill}" stroke="{stroke}" stroke-width="{l["w"]}" stroke-linecap="round" stroke-linejoin="round"/>')
    g.append('</g>')
    return "".join(g)
def css(c):
    h = c.lstrip('#')
    if len(h) == 8: return f'rgba({int(h[2:4],16)},{int(h[4:6],16)},{int(h[6:8],16)},{int(h[0:2],16)/255:.3f})'
    return '#' + h
if __name__ == '__main__':
    emit_kotlin(sys.argv[1])
    json.dump({k: [x0, y0, w, h, [dict(l, fill=l['fill'], stroke=l['stroke']) for l in ls]] for k, (x0, y0, w, h, ls) in D.items()}, open(sys.argv[2], 'w'))
