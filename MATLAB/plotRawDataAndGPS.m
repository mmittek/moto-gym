init;
if(~exist('dataLoaded'))
    dataLoaded = 1;
    data = loadCSVAndPreprocess('../Data/20160927_231506_654_from84andAHomeJacket.csv');
end




[x,y] = gpsToXYZ(data(:,LAT), data(:,LON), data(:,ALT));

figure(1)
plot(x,y);
axis equal;
grid on;