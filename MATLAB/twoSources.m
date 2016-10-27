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


figure(1)
plot( t, data1(:,2) );
hold on;
plot( t, data2(:,2) );
hold off;


