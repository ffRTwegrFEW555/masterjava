<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <h1>List of groups of "<xsl:value-of select='$param_project'/>" project.</h1>
        <xsl:text>&#xa;</xsl:text>
        <ul>
            <xsl:for-each select="//*
                    [name()='Group']
                    [translate(@parent, $uppercase, $smallcase) =
                        translate($param_project, $uppercase, $smallcase)]">
                <li>
                    <xsl:value-of select="text()"/>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:param name="param_project"/>
    <xsl:variable name="smallcase" select="'abcdefghijklmnopqrstuvwxyz'" />
    <xsl:variable name="uppercase" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />

</xsl:stylesheet>