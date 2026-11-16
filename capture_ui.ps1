Add-Type -AssemblyName System.Windows.Forms,System.Drawing
$process = Start-Process java -ArgumentList "-cp bin com.coffeeshop.Main" -PassThru
Start-Sleep -Seconds 3
$bounds = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
$bmp = New-Object System.Drawing.Bitmap $bounds.Width, $bounds.Height
$graphics = [System.Drawing.Graphics]::FromImage($bmp)
$graphics.CopyFromScreen($bounds.Location, [System.Drawing.Point]::Empty, $bounds.Size)
$destDir = "C:\Users\Fatih\.gemini\antigravity\brain\8a9ad1d5-caaa-485e-a8ff-645e50538cdd"
$bmp.Save("$destDir\screenshot_pos_kopi_kepikiran.png", [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Save("e:\CoffeeShop\screenshot_pos_kopi_kepikiran.png", [System.Drawing.Imaging.ImageFormat]::Png)
$graphics.Dispose()
$bmp.Dispose()
Stop-Process -Id $process.Id -Force
Write-Output "Screenshot captured successfully!"
