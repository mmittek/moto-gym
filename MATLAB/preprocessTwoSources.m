% This script is used to preprocess two sources of data from the app
% generated mostly for the dataset collected on Oct 26 2016
% with LG G3 laying in the tank bag screen up and pointing to the left
% and TURBO in the left jacket pocket facing screen toward the chest


root = '/Users/mmittek/Downloads/motogymrecords';
src1 = fullfile(root, 'TURBO_2016-09-26 20_26_55_430.csv');
src2 = fullfile(root, 'LG_2016-09-26 21_27_01_539.csv');
data1 = csvread(src1, 1);
data2 = csvread(src2,1);
data1 = sortrows(data1,1);
data2 = sortrows(data2,1);
data1(:,1) = data1(:,1)-data1(1,1);
data2(:,1) = data2(:,1)-data2(1,1);



N = max([ size(data1,1) size(data2,1) ]);   % number of samples
tmin = min( [data1(1,1) ,data2(1,1) ]);
tmax = max( [data1(end,1) ,data2(end,1) ]);
t = linspace(tmin, tmax, N);

[data1t, data1] = averageForUniqueTimebase( data1(:,1), data1(:,2:end) );
[data2t, data2] = averageForUniqueTimebase( data2(:,1), data2(:,2:end) );

% Interpolated data
data1 = [t' interp1(data1t,data1,t)];
data2 = [t' interp1(data2t,data2,t)];

data1(isnan(data1)) = 0;
data2(isnan(data2)) = 0;

 figure(3)
xc = xcorr( data1(:,2), data2(:,2), 1000 );
plot(xc);

% Determined empirically by looking at xcorr and testing for peaks
shift = 222;
data1 = data1(shift:end,:);
data2 = data2(1:size(data1,1),:);
t = data1(:,1);

figure(1)
plot( t, data1(:,2) );
hold on;
plot( t, data2(:,2) );
hold off;

save('20161026_two_sources_prep.mat', 't', 'data1', 'data2');



