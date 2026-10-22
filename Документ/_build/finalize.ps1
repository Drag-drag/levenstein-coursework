param(
    [Parameter(Mandatory = $true)][string]$Docx
)

$ErrorActionPreference = 'Stop'

$docx = (Resolve-Path $Docx).Path
$pdf = Join-Path $env:TEMP 'opencode\kursovaya.pdf'

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0
$word.AutomationSecurity = 3
try {
    $doc = $word.Documents.Open($docx, $false, $false)

    $doc.Fields.Update() | Out-Null
    if ($doc.TablesOfContents.Count -gt 0) {
        $doc.TablesOfContents.Item(1).Update()
    }

    if ($doc.TablesOfContents.Count -gt 0) {
        $r = $doc.TablesOfContents.Item(1).Range
        $r.Font.Name = 'Times New Roman'
        $r.Font.Size = 12
        $r.ParagraphFormat.Alignment = 0
        $r.ParagraphFormat.LineSpacingRule = 0
        $r.ParagraphFormat.SpaceBefore = 0
        $r.ParagraphFormat.SpaceAfter = 3
        $r.ParagraphFormat.FirstLineIndent = 0
    }

    $pages = $doc.ComputeStatistics(2)
    $words = $doc.ComputeStatistics(0)

    $doc.Save()
    $doc.ExportAsFixedFormat($pdf, 17)
    $doc.Close(0)
}
finally {
    $word.Quit()
}

Write-Output "PAGES=$pages WORDS=$words PDF=$pdf"
