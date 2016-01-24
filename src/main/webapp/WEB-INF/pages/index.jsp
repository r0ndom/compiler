<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Compiler</title>
    <meta charset="utf-8"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/codemirror.css">
    <link href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/resources/js/codemirror.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/jquery.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/pascal.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
    <form action="<c:url value="/"/>" method="POST">
        <label for="code">Code</label>
        <textarea id="code" name="code">
        </textarea>

        <div class="form-group">
            <label for="result">Result:</label>
            <textarea class="form-control" rows="15" id="result" readonly>${result}</textarea>
        </div>
        <input class="btn btn-primary" type="submit">
    </form>
</div>
    <script>
        $(document).ready(function() {
            var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
                lineNumbers: true,
                mode: "text/x-pascal"
            }).getDoc().setValue('');
        });
    </script>
</body>
</html>