(window.webpackJsonp=window.webpackJsonp||[]).push([[57],{126:function(e,n,t){"use strict";t.r(n),t.d(n,"frontMatter",(function(){return i})),t.d(n,"metadata",(function(){return c})),t.d(n,"toc",(function(){return b})),t.d(n,"default",(function(){return o}));var a=t(3),r=t(7),l=(t(0),t(253)),i={id:"feel-expression",title:"Expressions"},c={unversionedId:"reference/language-guide/feel-expression",id:"version-1.10/reference/language-guide/feel-expression",isDocsHomePage:!1,title:"Expressions",description:"An expression can contain literals, operators and function calls.",source:"@site/versioned_docs/version-1.10/reference/language-guide/feel-expression.md",slug:"/reference/language-guide/feel-expression",permalink:"/feel-scala/docs/1.10/reference/language-guide/feel-expression",editUrl:"https://github.com/camunda/feel-scala/edit/master/docs/versioned_docs/version-1.10/reference/language-guide/feel-expression.md",version:"1.10",sidebar:"version-1.10/Reference",previous:{title:"Unary-Tests",permalink:"/feel-scala/docs/1.10/reference/language-guide/feel-unary-tests"},next:{title:"Conversion Functions",permalink:"/feel-scala/docs/1.10/reference/builtin-functions/feel-built-in-functions-conversion"}},b=[{value:"Literal",id:"literal",children:[]},{value:"Path Expression",id:"path-expression",children:[]},{value:"Addition",id:"addition",children:[]},{value:"Subtraction",id:"subtraction",children:[]},{value:"Multiplication",id:"multiplication",children:[]},{value:"Division",id:"division",children:[]},{value:"Exponentiation",id:"exponentiation",children:[]},{value:"Comparison",id:"comparison",children:[]},{value:"Disjunction and Conjunction",id:"disjunction-and-conjunction",children:[]},{value:"If Expression",id:"if-expression",children:[]},{value:"For Expressions",id:"for-expressions",children:[]},{value:"Some/Every Expression",id:"someevery-expression",children:[]},{value:"Filter Expression",id:"filter-expression",children:[]},{value:"Evaluate a Unary Tests",id:"evaluate-a-unary-tests",children:[]},{value:"Instance-Of Expression",id:"instance-of-expression",children:[]},{value:"Functions",id:"functions",children:[]},{value:"Special Properties",id:"special-properties",children:[]}],s={toc:b};function o(e){var n=e.components,t=Object(r.a)(e,["components"]);return Object(l.b)("wrapper",Object(a.a)({},s,t,{components:n,mdxType:"MDXLayout"}),Object(l.b)("p",null,"An expression can contain literals, operators and function calls."),Object(l.b)("h3",{id:"literal"},"Literal"),Object(l.b)("p",null,"A single value of one of the ",Object(l.b)("a",Object(a.a)({parentName:"p"},{href:"/feel-scala/docs/1.10/reference/language-guide/feel-data-types"}),"types"),"."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'null\n21\n"valid"\n')),Object(l.b)("h3",{id:"path-expression"},"Path Expression"),Object(l.b)("p",null,"Access a value by its name/path. For example, a given variable from the input/context."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"x + y\n")),Object(l.b)("p",null,"If the value is a context (or data object/POJO) then the inner values can be accessed by ",Object(l.b)("inlineCode",{parentName:"p"},"context.key"),"."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"x.y\n// return 1 if x is {y: 1}\n")),Object(l.b)("p",null,"Also, directly on a context."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'{x: 2}.x\n// 2\n\n{x: {y: "valid"}}.x\n// {y: "valid"}\n\n{x: {y: "valid"}}.x.y\n// "valid"\n')),Object(l.b)("p",null,"Inside a context, the previous values can be accessed."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"{\n  a: 1,\n  b: 2,\n  c: a + b\n}\n")),Object(l.b)("p",null,"If the name or path contains any special character (e.g. whitespace, dash, etc.) then the name needs to be wrapped into single backquotes/backtick ",Object(l.b)("inlineCode",{parentName:"p"},"`foo bar`"),"."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"`name with whitespace`.`name+operator`\n")),Object(l.b)("h3",{id:"addition"},"Addition"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"supported types: number, string, day-time-duration, year-month-duration")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'2 + 3\n// 5\n\n"foo" + "bar"\n// "foobar"\n\nduration("P1D") + duration("PT6H")\n// duration("P1DT6H")\n')),Object(l.b)("h3",{id:"subtraction"},"Subtraction"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"supported types: number, time, date-time, day-time-duration, year-month-duration")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'5 - 3\n// 2\n\ntime("10:30:00") - time("09:00:00")\n// duration("PT1H30M")\n\ntime("10:30:00") - duration("PT1H") \n// time("09:30:00")\n')),Object(l.b)("h3",{id:"multiplication"},"Multiplication"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"supported types: number, day-time-duration, year-month-duration")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'5 * 3        \n// 15\n\n3 * duration("P2Y")      \n// duration("P6Y") \n')),Object(l.b)("h3",{id:"division"},"Division"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"supported types: number, day-time-duration, year-month-duration")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'6 / 2  \n// 3\n\nduration("P1Y") / 2 \n// duration("P6M")\n\nduration("P1Y") / duration("P1M")\n// 12\n')),Object(l.b)("h3",{id:"exponentiation"},"Exponentiation"),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"supported types: number")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"2 ** 3   \n// 8\n")),Object(l.b)("h3",{id:"comparison"},"Comparison"),Object(l.b)("table",null,Object(l.b)("thead",{parentName:"table"},Object(l.b)("tr",{parentName:"thead"},Object(l.b)("th",Object(a.a)({parentName:"tr"},{align:null}),"operator"),Object(l.b)("th",Object(a.a)({parentName:"tr"},{align:null}),"symbol"),Object(l.b)("th",Object(a.a)({parentName:"tr"},{align:null}),"example"))),Object(l.b)("tbody",{parentName:"table"},Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"equal to"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"=")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},'x = "valid"'))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"not equal to"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"!=")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},'x != "valid"'))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"less than"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"<")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"< 10"))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"less than or equal"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"<=")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"<= 10"))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"greater than"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},">")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"> 10"))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"greater than or equal"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},">=")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},">= 10"))),Object(l.b)("tr",{parentName:"tbody"},Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),"between"),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"between _ and _")),Object(l.b)("td",Object(a.a)({parentName:"tr"},{align:null}),Object(l.b)("inlineCode",{parentName:"td"},"x between 3 and 9"))))),Object(l.b)("p",null,"The operators less than, greater than, and between are only supported for: "),Object(l.b)("ul",null,Object(l.b)("li",{parentName:"ul"},"number"),Object(l.b)("li",{parentName:"ul"},"date"),Object(l.b)("li",{parentName:"ul"},"time"),Object(l.b)("li",{parentName:"ul"},"date-time"),Object(l.b)("li",{parentName:"ul"},"year-month-duration"),Object(l.b)("li",{parentName:"ul"},"day-time-duration ")),Object(l.b)("h3",{id:"disjunction-and-conjunction"},"Disjunction and Conjunction"),Object(l.b)("p",null,"Combine two boolean values."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"true and true   \n// true\n\ntrue and false        \n// false\n\ntrue and null        \n// null\n\nfalse and null\n// false\n")),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"true or false   \n// true\n\nfalse or false  \n// false\n\ntrue or null   \n// true\n\nfalse or null  \n// null\n")),Object(l.b)("h3",{id:"if-expression"},"If Expression"),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'if (x < 5) then "low" else "high"\n')),Object(l.b)("h3",{id:"for-expressions"},"For Expressions"),Object(l.b)("p",null,"Iterate over a list and apply an expression (i.e. aka ",Object(l.b)("inlineCode",{parentName:"p"},"map"),"). The result is again a list."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"for x in [1,2] return x * 2 \n// [2,4]\n")),Object(l.b)("p",null,"Iterate over multiple lists."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"for x in [1,2], y in [3,4] return x * y  \n// [3,4,6,8]\n")),Object(l.b)("p",null,"Iterate over a range - forward or backward."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"for x in 1..3 return x * 2                  \n// [2,4,6]\n\nfor x in 3..1 return x * 2       \n// [6,4,2]\n")),Object(l.b)("p",null,"The previous results of the iterator can be accessed by the variable ",Object(l.b)("inlineCode",{parentName:"p"},"partial"),". "),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"for x in 1..5 return x + sum(partial)       \n// [1,3,7,15,31]\n")),Object(l.b)("h3",{id:"someevery-expression"},"Some/Every Expression"),Object(l.b)("p",null,"Test if at least one element of the list satisfies the expression."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"some x in [1,2,3] satisfies x > 2         \n// true\n\nsome x in [1,2,3] satisfies x > 3   \n// false\n\nsome x in [1,2], y in [2,3] satisfies x < y  \n// true\n")),Object(l.b)("p",null,"Test if all elements of the list satisfies the expression."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"every x in [1,2,3] satisfies x >= 1   \n// true\n\nevery x in [1,2,3] satisfies x >= 2     \n// false\n\nevery x in [1,2], y in [2,3] satisfies x < y \n// false\n")),Object(l.b)("h3",{id:"filter-expression"},"Filter Expression"),Object(l.b)("p",null,"Filter a list of elements by an expression. The expression can access the current element by ",Object(l.b)("inlineCode",{parentName:"p"},"item"),". The result is a list again."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"[1,2,3,4][item > 2]   \n// [3,4]\n")),Object(l.b)("p",null,"An element of a list can be accessed by its index. The index starts at ",Object(l.b)("inlineCode",{parentName:"p"},"1"),". A negative index starts at the end by ",Object(l.b)("inlineCode",{parentName:"p"},"-1"),"."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"[1,2,3,4][1]           \n// 1\n\n[1,2,3,4][4]                                   \n// 4\n\n[1,2,3,4][-1]                                  \n// 4\n\n[1,2,3,4][-2]                                  \n// 3\n\n[1,2,3,4][5]                                   \n// null\n")),Object(l.b)("p",null,"If the elements are contextes then the nested value of the current element can be accessed directly by its name."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'[ {a: "foo", b: 5},  {a: "bar", b: 10} ][b > 7] \n// {a : "bar", b: 10}\n')),Object(l.b)("p",null,"The nested values of a specific key can be extracted by ",Object(l.b)("inlineCode",{parentName:"p"},".key"),"."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'[ {a : "foo", b: 5 }, {a: "bar", b: 10} ].a     \n// ["foo", "bar"]\n')),Object(l.b)("h3",{id:"evaluate-a-unary-tests"},"Evaluate a Unary Tests"),Object(l.b)("p",null,"Evaluates a ",Object(l.b)("a",Object(a.a)({parentName:"p"},{href:"feel-unary-tests"}),"unary-tests expression")," with the given value. "),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"x in (2..4)\n\nx in < 3\n")),Object(l.b)("h3",{id:"instance-of-expression"},"Instance-Of Expression"),Object(l.b)("p",null,"Checks the type of the value."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'"foo" instance of number                      \n// false\n\n"bar" instance of string                            \n// true\n')),Object(l.b)("h3",{id:"functions"},"Functions"),Object(l.b)("p",null,"Invoke a user-defined or built-in function by its name. The arguments can be passed positional or named."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"add(1,2)\n// or\nadd(x:1, y:2)\n")),Object(l.b)("p",null,"A function (body) can be defined using ",Object(l.b)("inlineCode",{parentName:"p"},"function(arguments) expression"),". For example, inside a context. "),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),"{\n    add : function(x,y) x + y\n}\n")),Object(l.b)("p",null,"It is also possible to define an external function which calls a Java method. "),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'function(x,y) external { \n    java: { \n        class: "java.lang.Math", \n        method signature: "max(int, int)" \n    } \n}\n')),Object(l.b)("h3",{id:"special-properties"},"Special Properties"),Object(l.b)("p",null,"Values of type date, time, date-time and duration have special properties to access their individual parts."),Object(l.b)("pre",null,Object(l.b)("code",Object(a.a)({parentName:"pre"},{className:"language-js"}),'date("2017-03-10").year                   \ndate("2017-03-10").month                \ndate("2017-03-10").day\n\ntime("11:45:30+02:00").hour            \ntime("11:45:30+02:00").minute         \ntime("11:45:30+02:00").second        \n\ndate and time("2017-03-10T11:45:30+02:00").year\ndate and time("2017-03-10T11:45:30+02:00").month\ndate and time("2017-03-10T11:45:30+02:00").day\ndate and time("2017-03-10T11:45:30+02:00").weekday\ndate and time("2017-03-10T11:45:30+02:00").hour\ndate and time("2017-03-10T11:45:30+02:00").minute\ndate and time("2017-03-10T11:45:30+02:00").second\n\nduration("P2Y3M").years                  \nduration("P2Y3M").months               \n\nduration("P1DT2H10M30S").days      \nduration("P1DT2H10M30S").hours     \nduration("P1DT2H10M30S").minutes \nduration("P1DT2H10M30S").seconds \n')))}o.isMDXComponent=!0},253:function(e,n,t){"use strict";t.d(n,"a",(function(){return u})),t.d(n,"b",(function(){return j}));var a=t(0),r=t.n(a);function l(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function c(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){l(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function b(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var s=r.a.createContext({}),o=function(e){var n=r.a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):c(c({},n),e)),t},u=function(e){var n=o(e.components);return r.a.createElement(s.Provider,{value:n},e.children)},p={inlineCode:"code",wrapper:function(e){var n=e.children;return r.a.createElement(r.a.Fragment,{},n)}},d=r.a.forwardRef((function(e,n){var t=e.components,a=e.mdxType,l=e.originalType,i=e.parentName,s=b(e,["components","mdxType","originalType","parentName"]),u=o(t),d=a,j=u["".concat(i,".").concat(d)]||u[d]||p[d]||l;return t?r.a.createElement(j,c(c({ref:n},s),{},{components:t})):r.a.createElement(j,c({ref:n},s))}));function j(e,n){var t=arguments,a=n&&n.mdxType;if("string"==typeof e||a){var l=t.length,i=new Array(l);i[0]=d;var c={};for(var b in n)hasOwnProperty.call(n,b)&&(c[b]=n[b]);c.originalType=e,c.mdxType="string"==typeof e?e:a,i[1]=c;for(var s=2;s<l;s++)i[s]=t[s];return r.a.createElement.apply(null,i)}return r.a.createElement.apply(null,t)}d.displayName="MDXCreateElement"}}]);